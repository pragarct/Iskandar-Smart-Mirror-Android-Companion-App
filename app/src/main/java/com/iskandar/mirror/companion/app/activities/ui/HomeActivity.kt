package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.alarms.AlarmOverviewActivity
import com.iskandar.mirror.companion.app.activities.ui.reminders.RemindersOverviewActivity
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_home)

        // This is a test commit, no actual changes

        // Set up listeners for tile buttons
        alarms_button.setOnClickListener {
            var intent = Intent(this, AlarmOverviewActivity::class.java)
            intent = getAlarms(intent)
            startActivity(intent)
        }
        reminders_button.setOnClickListener {
            var intent = Intent(this, RemindersOverviewActivity::class.java)
            intent = getReminders(intent)
            startActivity(intent)
        }
        background_button.setOnClickListener {
            var intent = Intent(this, BackgroundOverviewActivity::class.java)
            intent = getBackgroundImage(intent)
            startActivity(intent)
        }
        lighting_button.setOnClickListener {
            val intent = Intent(this, LightingActivity::class.java)
            getLightingSettings(intent)
        }
        events_button.setOnClickListener {
            getEvents()
        }
        change_information_button.setOnClickListener {
            val intent = Intent(this, ChangeLocationActivity::class.java)
            getWeatherAndTrafficInformation(intent)
        }
        bluetooth_button.setOnClickListener {
            var intent = Intent(this, BluetoothActivity::class.java)
            intent = getBluetoothInformation(intent)
            startActivity(intent)
        }
    }
}
