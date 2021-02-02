package com.iskandar.mirror.companion.app.activities.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Navigation Bar Setup
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        side_nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> Toast.makeText(applicationContext, "Clicked Item 1", Toast.LENGTH_SHORT).show()
                R.id.miItem2 -> Toast.makeText(applicationContext, "Clicked Item 2", Toast.LENGTH_SHORT).show()
                R.id.miItem3 -> Toast.makeText(applicationContext, "Clicked Item 3", Toast.LENGTH_SHORT).show()
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
}
