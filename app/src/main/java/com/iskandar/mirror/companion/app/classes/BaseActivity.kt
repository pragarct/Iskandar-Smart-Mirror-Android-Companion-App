package com.iskandar.mirror.companion.app.classes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.BackgroundOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.ChangeLocationActivity
import com.iskandar.mirror.companion.app.activities.ui.HomeActivity
import com.iskandar.mirror.companion.app.activities.ui.LightingActivity
import com.iskandar.mirror.companion.app.activities.ui.alarms.AlarmOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.events.EventsOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.reminders.RemindersOverviewActivity
import kotlinx.android.synthetic.main.activity_home.*

open class BaseActivity : AppCompatActivity() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }*/

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
                    var intent = Intent(this, LightingActivity::class.java)
                    intent = getLightingSettings(intent)
                    startActivity(intent)
                }
                R.id.nav_events -> {
                    var intent = Intent(this, EventsOverviewActivity::class.java)
                    intent = getEvents(intent)
                    startActivity(intent)
                }
                R.id.nav_change_information -> {
                    var intent = Intent(this, ChangeLocationActivity::class.java)
                    intent = getWeatherAndTrafficInformation(intent)
                    startActivity(intent)
                }
            }
            true
        }
    }

    fun getAlarms(intent: Intent): Intent {
        return intent
    }

    fun getReminders(intent: Intent): Intent {
        return intent
    }

    fun getBackgroundImage(intent: Intent): Intent {
        return intent
    }

    fun getLightingSettings(intent: Intent): Intent {
        val color = "#FFA2DC"
        intent.putExtra("color", color)
        return intent
    }

    fun getEvents(intent: Intent): Intent {
        return intent
    }

    fun getWeatherAndTrafficInformation(intent: Intent): Intent {
        val zipCode = "45233"
        val city = "Cincinnati"
        val homeAddress = "858 Braemore Lane"
        val workSchoolAddress = "4900 Waterstone Boulevard"

        intent.putExtra("zipCode", zipCode)
        intent.putExtra("city", city)
        intent.putExtra("homeAddress", homeAddress)
        intent.putExtra("workSchoolAddress", workSchoolAddress)

        return intent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    // End Navigation Bar

    // Close the keyboard on button/screen press and clear focus
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)) {
            if (view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
                val scrcoords = IntArray(2)
                view.getLocationOnScreen(scrcoords)
                val x = ev.rawX + view.getLeft() - scrcoords[0]
                val y = ev.rawY + view.getTop() - scrcoords[1]

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

    // Close the keyboard when screen is touched (NEED TO LOSE FOCUS TOO)
    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        // Close the keyboard on screen press
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }*/

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
