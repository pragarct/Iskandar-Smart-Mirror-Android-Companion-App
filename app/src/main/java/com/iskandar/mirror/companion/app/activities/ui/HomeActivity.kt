package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import com.iskandar.mirror.companion.app.R
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
        lighting_button.setOnClickListener {
            val intent = Intent(this, LightingActivity::class.java)
            getRequest(intent, this, "lighting")
        }
        events_button.setOnClickListener {
            fromCalendarApp = true
            openGoogleCalenderApp()
        }
        background_button.setOnClickListener {
            createDialog(
                getString(R.string.coming_soon_desc),
                getString(R.string.okay),
                getString(R.string.coming_soon)
            )
        }
        ical_link_button.setOnClickListener {
            val intent = Intent(this, ICalActivity::class.java)
            getRequest(intent, this, "calendar")
        }
        change_location_button.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            getRequest(intent, this, "location")
        }
        bluetooth_button.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)
        }
        settings_button.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        help_button.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
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
