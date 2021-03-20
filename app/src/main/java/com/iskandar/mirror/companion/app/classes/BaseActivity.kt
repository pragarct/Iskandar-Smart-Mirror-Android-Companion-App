package com.iskandar.mirror.companion.app.classes

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.BluetoothActivity
import com.iskandar.mirror.companion.app.activities.ui.HelpActivity
import com.iskandar.mirror.companion.app.activities.ui.HomeActivity
import com.iskandar.mirror.companion.app.activities.ui.ICalActivity
import com.iskandar.mirror.companion.app.activities.ui.LightingActivity
import com.iskandar.mirror.companion.app.activities.ui.LocationActivity
import com.iskandar.mirror.companion.app.activities.ui.SettingsActivity
import com.iskandar.mirror.companion.app.data.IPAddressSharedPreference
import com.iskandar.mirror.companion.app.data.IsConfiguredSharedPreference
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private var fromCalendarApp = false
// private var IP_Address = "http://10.0.2.2:5000/"

open class BaseActivity : AppCompatActivity() {
    // Navigation Bar
    lateinit var toggle: ActionBarDrawerToggle

    fun setUpNavigationBar() {
        // Navigation Bar Setup
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_lighting -> {
                    val intent = Intent(this, LightingActivity::class.java)
                    getRequest(intent, this, "lighting")
                }
                R.id.nav_events -> {
                    fromCalendarApp = true
                    openGoogleCalenderApp()
                }
                R.id.nav_background -> {
                    createDialog(
                        getString(R.string.coming_soon_desc),
                        getString(R.string.okay),
                        getString(R.string.coming_soon)
                    )
                }
                R.id.nav_ical_link -> {
                    val intent = Intent(this, ICalActivity::class.java)
                    getRequest(intent, this, "calendar")
                }
                R.id.nav_location -> {
                    val intent = Intent(this, LocationActivity::class.java)
                    getRequest(intent, this, "location")
                }
                R.id.nav_bluetooth -> {
                    val intent = Intent(this, BluetoothActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_help -> {
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onResume() {
        if (fromCalendarApp) {
            fromCalendarAppDialogBuilder()
        }
        fromCalendarApp = false
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    // End Navigation Bar

    fun openGoogleCalenderApp() {
        val calendarUri = CalendarContract.CONTENT_URI
            .buildUpon()
            .appendPath("time")
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, calendarUri))
    }

    fun fromCalendarAppDialogBuilder() {
        val dialogBuilder = AlertDialog.Builder(this)

        // Set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.refresh_mirror_confirm))
            // If the dialog is cancelable
            .setCancelable(false)
            // Positive button text and action
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val url = getString(R.string.server_url) + "refresh"
                putRequest(this, intent, url, "refresh", false)
            }
            // Negative button text and action
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }

        // Create dialog box
        val alert = dialogBuilder.create()
        // Set title for alert dialog box
        alert.setTitle(getString(R.string.welcome_back))
        // Show alert dialog
        alert.show()
    }

    fun getRequest(intent: Intent, context: Context, urlParam: String) {
        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val url = getString(R.string.server_url) + urlParam

            val future = RequestFuture.newFuture<JSONObject>()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, JSONObject(), future, future)
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)

            try {
                val response = future.get(500, TimeUnit.MILLISECONDS) // this will block
                var welcomeConfigure = false

                // Welcome Activity
                if (urlParam == "configured") {
                    welcomeConfigure = response.get("is_configured").toString().toBoolean()
                }
                // Lighting Activity
                if (urlParam == "lighting") {
                    val red = response.get("red")
                    val green = response.get("green")
                    val blue = response.get("blue")
                    val brightness = response.get("brightness")

                    var hex = java.lang.String.format("#%02x%02x%02x", red, green, blue)
                    hex = hex.toUpperCase(Locale.ROOT)

                    intent.putExtra("color", hex)
                    intent.putExtra("brightness", brightness.toString())
                }
                // iCal Activity
                if (urlParam == "calendar") {
                    val iCal = response.get("ical").toString()
                    intent.putExtra("iCalURL", iCal)
                    intent.putExtra("initialSetup", false)
                }
                // Location Activity
                if (urlParam == "location") {
                    intent.putExtra("city", response.getString("city"))
                    intent.putExtra("state", response.getString("state"))
                    intent.putExtra("zipCode", response.getString("zip"))
                    intent.putExtra("units", response.getString("units"))
                    intent.putExtra("homeAddress", response.getString("home"))
                    intent.putExtra("workSchoolAddress", response.getString("work"))
                }

                // Start activity unless its the welcome activity and file is not configured
                if (urlParam != "configured" || welcomeConfigure) {
                    startActivity(intent)
                }
                // Prevent using from backing if initial setup is complete
                if (urlParam == "configured" && welcomeConfigure) {
                    finish()
                }
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_get_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: ExecutionException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_get_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: TimeoutException) {
                runOnUiThread {
                    if (urlParam == "configured") {
                        val isConfigured = getIsConfigured()
                        if (isConfigured) {
                            createDialog(
                                getString(R.string.welcome_connection_error_desc),
                                getString(R.string.okay),
                                getString(R.string.welcome_connection_error_title)
                            )
                            retryConnectionButton.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.rest_get_error), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun putRequest(context: Context, intent: Intent, url: String, param: String, initialSetup: Boolean) {
        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)

            val future = RequestFuture.newFuture<String>()
            val stringRequest = StringRequest(Request.Method.PUT, url, future, future)
            // Add the request to the RequestQueue.
            queue.add(stringRequest)

            try {
                val response = future.get(500, TimeUnit.MILLISECONDS) // this will block
                Log.d("Response", response)

                // Lighting Activity
                if (param == "lighting") {
                    // Build alert dialog
                    runOnUiThread {
                        val dialogBuilder = AlertDialog.Builder(context)

                        // Set message of alert dialog
                        dialogBuilder.setMessage(getString(R.string.keep_color_confirm))
                            // If the dialog is cancelable
                            .setCancelable(false)
                            // Positive button text and action
                            .setPositiveButton(getString(R.string.keep_color)) { _, _ ->
                                startActivity(intent)
                            }
                            // Negative button text and action
                            .setNegativeButton(getString(R.string.choose_new_color)) { dialog, _ -> dialog.cancel() }

                        // Create dialog box
                        val alert = dialogBuilder.create()
                        // Set title for alert dialog box
                        alert.setTitle(getString(R.string.color_confirmation))
                        // Show alert dialog
                        alert.show()
                    }
                    // Other Activities
                } else {
                    startActivity(intent)
                    if (initialSetup) {
                        finish()
                    }
                }
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: ExecutionException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: TimeoutException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
                Log.d("Response", "TIMEOUT")
            }
        }
    }

    // Close the keyboard on button/screen press and clear focus
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)) {
            if (view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
                val scrCoords = IntArray(2)
                view.getLocationOnScreen(scrCoords)
                val x = ev.rawX + view.getLeft() - scrCoords[0]
                val y = ev.rawY + view.getTop() - scrCoords[1]

                if (x < view.getLeft() || x > view.getRight()) {
                    (this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        this.window.decorView.applicationWindowToken,
                        0
                    )
                    view.clearFocus()
                }
                if (y < view.getTop() || y > view.getBottom()) {
                    (this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        this.window.decorView.applicationWindowToken,
                        0
                    )
                    view.clearFocus()
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /*fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    // Used to close the keyboard - hideKeyboard()
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }*/

    // Used to add button for clearing EditText
    fun addRightCancelDrawable(editText: EditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.cancel)
        cancel?.setBounds(0, 0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        editText.setCompoundDrawables(null, null, cancel, null)
    }

    fun getIPAddress(): String? {
        val myPreference = IPAddressSharedPreference(this)
        return myPreference.getIPAddress()
    }

    fun setIPAddress(ipAddressInput: String) {
        val myPreference = IPAddressSharedPreference(this)
        myPreference.setIPAddress(ipAddressInput)
        // IP_Address = ipAddressInput
    }

    fun getIsConfigured(): Boolean {
        val myPreference = IsConfiguredSharedPreference(this)
        return myPreference.getIsConfigured()
    }

    fun setIsConfigured(isConfiguredInput: Boolean) {
        val myPreference = IsConfiguredSharedPreference(this)
        myPreference.setIsConfigured(isConfiguredInput)
        // IS_Configured = isConfigured
    }

    fun createDialog(message: String, negative: String, title: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        // Set message of alert dialog
        dialogBuilder.setMessage(message)
            // If the dialog is cancelable
            .setCancelable(true)
            // Negative button text and action
            .setNegativeButton(negative) { dialog, _ -> dialog.cancel() }

        // Create dialog box
        val alert = dialogBuilder.create()
        // Set title for alert dialog box
        alert.setTitle(title)
        // Show alert dialog
        alert.show()
    }
}
