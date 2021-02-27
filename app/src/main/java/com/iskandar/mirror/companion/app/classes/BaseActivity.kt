package com.iskandar.mirror.companion.app.classes

import android.app.Activity
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
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.BackgroundOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.BluetoothActivity
import com.iskandar.mirror.companion.app.activities.ui.ChangeGmailActivity
import com.iskandar.mirror.companion.app.activities.ui.ChangeLocationActivity
import com.iskandar.mirror.companion.app.activities.ui.HomeActivity
import com.iskandar.mirror.companion.app.activities.ui.LightingActivity
import com.iskandar.mirror.companion.app.activities.ui.alarms.AlarmOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.reminders.RemindersOverviewActivity
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
                R.id.nav_alarms -> {
                    val intent = Intent(this, AlarmOverviewActivity::class.java)
                    startActivity(intent)
                    // getRequest(intent, this, "alarms")
                }
                R.id.nav_reminders -> {
                    val intent = Intent(this, RemindersOverviewActivity::class.java)
                    startActivity(intent)
                    // getRequest(intent, this, "reminders")
                }
                R.id.nav_background -> {
                    val intent = Intent(this, BackgroundOverviewActivity::class.java)
                    startActivity(intent)
                    // getRequest(intent, this, "background_image")
                }
                R.id.nav_lighting -> {
                    val intent = Intent(this, LightingActivity::class.java)
                    getRequest(intent, this, "lighting")
                }
                R.id.nav_events -> {
                    openGoogleCalenderApp()
                }
                R.id.nav_location -> {
                    val intent = Intent(this, ChangeLocationActivity::class.java)
                    getRequest(intent, this, "location")
                }
                R.id.nav_bluetooth -> {
                    val intent = Intent(this, BluetoothActivity::class.java)
                    startActivity(intent)
                    // getRequest(intent, this, "bluetooth")
                }
                R.id.nav_gmail -> {
                    val intent = Intent(this, ChangeGmailActivity::class.java)
                    // intent.putExtra("initialSetup", false)
                    // startActivity(intent)
                    getRequest(intent, this, "iCal")
                }
            }
            true
        }
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
                if (urlParam == "iCal") {
                    val iCal = response.get("iCal").toString()
                    intent.putExtra("iCal", iCal)
                    intent.putExtra("initialSetup", false)
                }
                // Location Activity
                if (urlParam == "location") {
                    intent.putExtra("city", response.getString("city"))
                    intent.putExtra("state", response.getString("state"))
                    intent.putExtra("zipCode", response.getString("zip"))
                    intent.putExtra("homeAddress", response.getString("home"))
                    intent.putExtra("workSchoolAddress", response.getString("work"))
                }

                startActivity(intent)
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
                    Toast.makeText(context, getString(R.string.rest_get_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun putRequest(context: Context, intent: Intent, url: String, param: String) {
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
                        dialogBuilder.setMessage("Do you want to keep this color?")
                            // If the dialog is cancelable
                            .setCancelable(false)
                            // Positive button text and action
                            .setPositiveButton("Keep Color") { _, _ ->
                                startActivity(intent)
                            }
                            // Negative button text and action
                            .setNegativeButton("Choose New Color") { dialog, _ -> dialog.cancel() }

                        // Create dialog box
                        val alert = dialogBuilder.create()
                        // Set title for alert dialog box
                        alert.setTitle("Color Confirmation")
                        // Show alert dialog
                        alert.show()
                    }
                } else {
                    startActivity(intent)
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
                Log.d("Response", "LIGHTING TIMEOUT")
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

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    // Used to close the keyboard - hideKeyboard()
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Used to add button for clearing EditText
    fun addRightCancelDrawable(editText: EditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.cancel)
        cancel?.setBounds(0, 0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        editText.setCompoundDrawables(null, null, cancel, null)
    }
}
