package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_ical.*
import kotlinx.android.synthetic.main.activity_ical.nav_view
import kotlinx.android.synthetic.main.activity_ical.submitButton

class ICalActivity : BaseActivity() {

    private var initialSetup = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ical)

        val initialSetupMaybeNull: Boolean? = intent.extras?.getBoolean("initialSetup")
        if (initialSetupMaybeNull != null) {
            initialSetup = initialSetupMaybeNull
        }

        if (initialSetup) {
            title = getString(R.string.iskandar_initial_setup)
        } else {
            setUpNavigationBar()
            // Highlights the item in the navigation bar
            nav_view.setCheckedItem(R.id.nav_ical_link)
        }

        setupListenersAndEditTexts()

        val iCalURL: String? = intent.extras?.getString("iCalURL")

        if (!iCalURL.isNullOrEmpty()) {
            // Set edit texts to currently set values
            googleiCalEditText.setText(iCalURL)
        }
    }

    private fun setupListenersAndEditTexts() {
        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(googleiCalEditText)
        googleiCalEditText.onRightDrawableClicked { it.text.clear() }
        googleiCalEditText.makeClearableEditText(null, null)

        // Submit is pressed, run data validation
        var initialDataIsValid = true
        submitButton.setOnClickListener {
            // This checks that the input matches the format of an iCal link
            if (googleiCalEditText.length() > 43) {
                if ((googleiCalEditText.text.substring(0, 42) != "https://calendar.google.com/calendar/ical/") ||
                    (!googleiCalEditText.text.contains("gmail.com/private")) ||
                    (!googleiCalEditText.text.endsWith("/basic.ics"))
                ) {
                    initialDataIsValid = false
                }
            } else {
                initialDataIsValid = false
            }

            // Data is valid, open up Home Activity
            if (initialDataIsValid) {
                val intent: Intent = if (initialSetup) {
                    setIsConfigured(true)
                    Intent(this, HomeActivity::class.java)
                } else {
                    Intent(this, HomeActivity::class.java)
                }

                putGoogleCalenderLink(intent, initialSetup)
            } else {
                val failedString = getString(R.string.ical_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
            }

            initialDataIsValid = true
        }
    }

    private fun putGoogleCalenderLink(intent: Intent, initialSetup: Boolean) {
        val iCalLink = googleiCalEditText.text.toString()
        val url = getString(R.string.server_url) + "calendar?url=$iCalLink"
        putRequest(this, intent, url, "calendar", initialSetup)
    }
}
