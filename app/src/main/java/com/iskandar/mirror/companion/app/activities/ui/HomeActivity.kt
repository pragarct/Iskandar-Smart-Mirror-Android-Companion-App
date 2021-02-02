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

        // Set up listeners for tile buttons
        alarms_button.setOnClickListener {
            val intent = Intent(this, AlarmOverviewActivity::class.java)
            startActivity(intent)
        }
        reminders_button.setOnClickListener {
            val intent = Intent(this, RemindersOverviewActivity::class.java)
            startActivity(intent)
        }
        background_button.setOnClickListener {
            val intent = Intent(this, BackgroundOverviewActivity::class.java)
            startActivity(intent)
        }
        change_information_button.setOnClickListener {
            val intent = Intent(this, ChangeInformationActivity::class.java)
            startActivity(intent)
        }
    }
}
