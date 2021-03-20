package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    // This is the loading time of the splash screen
    private val splashTimeout: Long = 750

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setSupportActionBar(findViewById(R.id.toolbar))

        val isConfigured = getIsConfigured()
        if (isConfigured) {
            welcomeTextView.text = getString(R.string.welcome_back)
        }

        retryConnectionButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            getRequest(intent, this, "configured")
            progressBar.visibility = View.VISIBLE
        }

        Handler().postDelayed(
            {
                // This method will be executed once the timer is over
                if (isConfigured) {
                    val intent = Intent(this, HomeActivity::class.java)
                    getRequest(intent, this, "configured")
                } else {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                }
            },
            splashTimeout
        )
    }
}
