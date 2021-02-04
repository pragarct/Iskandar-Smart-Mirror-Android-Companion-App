package com.iskandar.mirror.companion.app.classes

import android.content.Intent
import android.widget.Toast
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.HomeActivity
import kotlinx.android.synthetic.main.activity_initial_setup.*

open class InformationBaseActivity : BaseActivity() {
    // This is called in both InitialSetupActivity and ChangeInformationActivity
    fun setupListenersAndEditTexts() {
        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(zipCodeEditText)
        zipCodeEditText.onRightDrawableClicked { it.text.clear() }
        zipCodeEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(cityEditText)
        cityEditText.onRightDrawableClicked { it.text.clear() }
        cityEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(homeAddressEditText)
        homeAddressEditText.onRightDrawableClicked { it.text.clear() }
        homeAddressEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(workSchoolAddressEditText)
        workSchoolAddressEditText.onRightDrawableClicked { it.text.clear() }
        workSchoolAddressEditText.makeClearableEditText(null, null)

        // Submit is pressed, run data validation
        var initialDataIsValid = true
        submitButton.setOnClickListener {
            // ZIP code is not entered correctly
            if (zipCodeEditText.length() != 5) {
                val failedString = getString(R.string.zip_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No city was entered
            if (cityEditText.length() == 0) {
                val failedString = getString(R.string.city_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No home address was entered
            if (homeAddressEditText.length() == 0) {
                val failedString = getString(R.string.home_address_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No work or school address was entered
            if (workSchoolAddressEditText.length() == 0) {
                val failedString = getString(R.string.work_school_address_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // Data is valid, open up Home Activity
            if (initialDataIsValid) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

            initialDataIsValid = true
        }
    }
}
