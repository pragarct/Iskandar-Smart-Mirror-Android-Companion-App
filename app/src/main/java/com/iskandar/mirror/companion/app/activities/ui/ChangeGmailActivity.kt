package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_change_gmail.*
import kotlinx.android.synthetic.main.activity_change_gmail.nav_view
import kotlinx.android.synthetic.main.activity_change_gmail.submitButton

class ChangeGmailActivity : BaseActivity() {

    private var initialSetup = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_gmail)

        val initialSetupMaybeNull: Boolean? = intent.extras?.getBoolean("initialSetup")
        if (initialSetupMaybeNull != null) {
            initialSetup = initialSetupMaybeNull
        }

        if (initialSetup) {
            title = getString(R.string.iskandar_initial_setup)
        } else {
            setUpNavigationBar()
            // Highlights the item in the navigation bar
            nav_view.setCheckedItem(R.id.nav_gmail)
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
            // ZIP code is not entered correctly
            if (googleiCalEditText.length() == 0) {
                val failedString = getString(R.string.zip_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // Data is valid, open up Home Activity
            if (initialDataIsValid) {
                val intent: Intent = if (initialSetup) {
                    Intent(this, HomeActivity::class.java)
                } else {
                    Intent(this, HomeActivity::class.java)
                }

                // putGoogleCalenderLink(intent)
                // TO DO
                startActivity(intent)
            }

            initialDataIsValid = true
        }
    }

    private fun putGoogleCalenderLink() {
        val intent = Intent(this, HomeActivity::class.java)
        val iCalLink = googleiCalEditText.text.toString()
        val url = getString(R.string.server_url) + "iCal?rgb=$iCalLink"
        // TO DO similar setup to location setup for changing intent?
        putRequest(this, intent, url, "iCal")
    }
}
