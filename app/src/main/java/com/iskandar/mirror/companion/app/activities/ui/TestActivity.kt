package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.net.URL
import java.util.concurrent.Executors

class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Create listener for normally starting the app
        normalStart.setOnClickListener {
            val intent = Intent(this, InitialSetupActivity::class.java)
            startActivity(intent)
        }

        // Create listener for quick starting the app (default data)
        quickStart.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Create listener for quick test of data to send
        sendTestData.setOnClickListener {
            // Supposedly executors and handlers aren't a great way to handle this, change eventually
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.execute {
                // Need to user 10.0.2.2 for the emulator. I have no idea why.
                // We need to test on an actual device. I have a feeling 10.0.2.2 won't work
                val result = URL(getString(R.string.server_url) + "weather").readText()

                handler.post {
                    Log.d(javaClass.simpleName, result)
                }
            }
        }

        // Create listener for help floating action button
        findViewById<FloatingActionButton>(R.id.help_fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
