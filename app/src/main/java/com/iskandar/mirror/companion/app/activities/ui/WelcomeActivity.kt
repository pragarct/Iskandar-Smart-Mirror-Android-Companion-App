package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val isConfigured = getIsConfigured()
        if (isConfigured) {
            val configuredIntent = Intent(this, HomeActivity::class.java)
            getRequest(configuredIntent, this, "configured")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Create listener for normally starting the app
        getStartedButton.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)
        }

        retryConnectionButton.setOnClickListener {
            val configuredIntent = Intent(this, HomeActivity::class.java)
            getRequest(configuredIntent, this, "configured")
        }

        // Create listener for help floating action button
        findViewById<FloatingActionButton>(R.id.help_fab).setOnClickListener { view ->
            createDialog(
                getString(R.string.welcome_help_desc),
                getString(R.string.okay),
                getString(R.string.welcome_help_title)
            )
        }
    }
}
