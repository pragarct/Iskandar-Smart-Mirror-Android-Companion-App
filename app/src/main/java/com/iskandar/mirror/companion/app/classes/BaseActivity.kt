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
                    var intent = Intent(this, AlarmOverviewActivity::class.java)
                    intent = getAlarms(intent)
                    startActivity(intent)
                }
                R.id.nav_reminders -> {
                    var intent = Intent(this, RemindersOverviewActivity::class.java)
                    intent = getReminders(intent)
                    startActivity(intent)
                }
                R.id.nav_background -> {
                    var intent = Intent(this, BackgroundOverviewActivity::class.java)
                    intent = getBackgroundImage(intent)
                    startActivity(intent)
                }
                R.id.nav_lighting -> {
                    val intent = Intent(this, LightingActivity::class.java)
                    getLightingSettings(intent)
                }
                R.id.nav_events -> {
                    getEvents()
                }
                R.id.nav_location -> {
                    val intent = Intent(this, ChangeLocationActivity::class.java)
                    getWeatherAndTrafficInformation(intent)
                }
                R.id.nav_bluetooth -> {
                    var intent = Intent(this, BluetoothActivity::class.java)
                    intent = getBluetoothInformation(intent)
                    startActivity(intent)
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

    fun getAlarms(intent: Intent): Intent {
        return intent
    }

    fun getReminders(intent: Intent): Intent {
        return intent
    }

    fun getBackgroundImage(intent: Intent): Intent {
        return intent
    }

    fun getLightingSettings(intent: Intent) {
        // val color = "#FFA2DC"
        // intent.putExtra("color", color)
        // return intent

        val context = this

        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val url = getString(R.string.server_url) + "lighting"

            val future = RequestFuture.newFuture<JSONObject>()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, JSONObject(), future, future)
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)

            try {
                val response = future.get(500, TimeUnit.MILLISECONDS) // this will block

                val red = response.get("red")
                val green = response.get("green")
                val blue = response.get("blue")
                val brightness = response.get("brightness")

                var hex = java.lang.String.format("#%02x%02x%02x", red, green, blue)
                hex = hex.toUpperCase(Locale.ROOT)

                intent.putExtra("color", hex)
                intent.putExtra("brightness", brightness.toString())
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
                Log.d("Response", "LIGHTING TIMEOUT")
            }
        }
    }

    fun getEvents() {
        val calendarUri = CalendarContract.CONTENT_URI
            .buildUpon()
            .appendPath("time")
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, calendarUri))
    }

    fun getWeatherAndTrafficInformation(intent: Intent) {
        val context = this
        intent.putExtra("initialSetup", true)

        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val weatherUrl = getString(R.string.server_url) + "weather"

            val future1 = RequestFuture.newFuture<String>()
            val weatherStringRequest = StringRequest(weatherUrl, future1, future1)
            // Add the request to the RequestQueue.
            queue.add(weatherStringRequest)

            try {
                val response = future1.get(500, TimeUnit.MILLISECONDS) // this will block
                val splitResponse = response.toString().split("\"", ",")
                intent.putExtra("zipCode", splitResponse[2].substring(2))
                intent.putExtra("city", splitResponse[6])
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
                Log.d("Response", "WEATHER TIMEOUT")
            }

            val trafficUrl = getString(R.string.server_url) + "traffic"

            val future2 = RequestFuture.newFuture<String>()
            val trafficStringRequest = StringRequest(trafficUrl, future2, future2)
            queue.add(trafficStringRequest)

            try {
                val response = future2.get(500, TimeUnit.MILLISECONDS) // this will block
                val splitResponse = response.toString().split("\"", ",")
                intent.putExtra("homeAddress", splitResponse[3])
                intent.putExtra("workSchoolAddress", splitResponse[8])
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
                Log.d("Response", "TRAFFIC TIMEOUT")
            }
        }
    }

    /*fun getWeatherInformation(): Intent {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/weather"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val splitResponse = response.toString().split("\"", ",")
                zipCodeEditText.setText(splitResponse[2].substring(2))
                cityEditText.setText(splitResponse[6])
                this.intent
            },
            { Toast.makeText(this, R.string.rest_error, Toast.LENGTH_LONG).show() }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getTrafficInformation(): Intent {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/traffic"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val splitResponse = response.toString().split("\"", ",")
                intent.putExtra("zipCode", splitResponse[2].substring(2))
                intent.putExtra("city", splitResponse[6])
                return intent
            },
            { Toast.makeText(this, R.string.rest_error, Toast.LENGTH_LONG).show() }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }*/

    fun getBluetoothInformation(intent: Intent): Intent {
        return intent
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
