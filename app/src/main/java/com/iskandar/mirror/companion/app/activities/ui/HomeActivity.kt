package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.alarms.AlarmOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.reminders.RemindersOverviewActivity
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*

private var fromCalendarApp = false

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_home)

        // Set up listeners for tile buttons
        alarms_button.setOnClickListener {
            val intent = Intent(this, AlarmOverviewActivity::class.java)
            // getRequest(intent, this, "alarms")
            startActivity(intent)
        }
        reminders_button.setOnClickListener {
            val intent = Intent(this, RemindersOverviewActivity::class.java)
            // getRequest(intent, this, "reminders")
            startActivity(intent)
        }
        background_button.setOnClickListener {
            val intent = Intent(this, BackgroundOverviewActivity::class.java)
            // getRequest(intent, this, "background")
            startActivity(intent)
        }
        lighting_button.setOnClickListener {
            val intent = Intent(this, LightingActivity::class.java)
            getRequest(intent, this, "lighting")
        }
        events_button.setOnClickListener {
            fromCalendarApp = true
            openGoogleCalenderApp()
        }
        change_location_button.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            getRequest(intent, this, "location")
        }
        bluetooth_button.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            // getRequest(intent, this, "bluetooth")
            startActivity(intent)
        }
    }

    override fun onResume() {
        if (fromCalendarApp) {
            fromCalendarAppDialogBuilder()
        }
        fromCalendarApp = false
        super.onResume()
    }
}
