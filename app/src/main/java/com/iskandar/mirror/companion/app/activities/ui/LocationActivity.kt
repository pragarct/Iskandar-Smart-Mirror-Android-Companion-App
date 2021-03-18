package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : BaseActivity() {

    private var initialSetup = true
    private var stateCodes = arrayOfNulls<String>(52) //  // 50 states + DC + blank
    private var stateNames = arrayOfNulls<String>(52)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val initialSetupMaybeNull: Boolean? = intent.extras?.getBoolean("initialSetup")
        if (initialSetupMaybeNull != null) {
            initialSetup = initialSetupMaybeNull
        }

        if (initialSetup) {
            title = getString(R.string.iskandar_initial_setup)
        } else {
            setUpNavigationBar()
            // Highlights the item in the navigation bar
            nav_view.setCheckedItem(R.id.nav_location)
        }

        setupListenersAndEditTexts()

        val city: String? = intent.extras?.getString("city")
        val state: String? = intent.extras?.getString("state")
        val zipCode: String? = intent.extras?.getString("zipCode")
        val units: String? = intent.extras?.getString("units")
        val homeAddress: String? = intent.extras?.getString("homeAddress")
        val workSchoolAddress: String? = intent.extras?.getString("workSchoolAddress")

        if (!zipCode.isNullOrEmpty() && !city.isNullOrEmpty() &&
            !homeAddress.isNullOrEmpty() && !workSchoolAddress.isNullOrEmpty()
        ) {
            // Set edit texts (and state spinner) to currently set values
            cityEditText.setText(city)
            val statePos = stateCodes.indexOf(state)
            stateSpinner.setSelection(statePos)
            zipCodeEditText.setText(zipCode)
            homeAddressEditText.setText(homeAddress)
            workSchoolAddressEditText.setText(workSchoolAddress)
            // Set the radio button to the set values
            if (units == "imperial") { fahrenheit.isChecked = true }
            if (units == "celsius") { celsius.isChecked = true }
            if (units == "kelvin") { kelvin.isChecked = true }
        }
    }

    private fun setupListenersAndEditTexts() {
        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(cityEditText)
        cityEditText.onRightDrawableClicked { it.text.clear() }
        cityEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(zipCodeEditText)
        zipCodeEditText.onRightDrawableClicked { it.text.clear() }
        zipCodeEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(homeAddressEditText)
        homeAddressEditText.onRightDrawableClicked { it.text.clear() }
        homeAddressEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(workSchoolAddressEditText)
        workSchoolAddressEditText.onRightDrawableClicked { it.text.clear() }
        workSchoolAddressEditText.makeClearableEditText(null, null)

        // State Spinner
        stateNames = resources.getStringArray(R.array.states)
        stateCodes = resources.getStringArray(R.array.stateCodes)

        val dropdown = findViewById<View>(R.id.stateSpinner) as Spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            stateNames
        )
        dropdown.adapter = adapter
        // End State Spinner

        // Submit is pressed, run data validation
        var initialDataIsValid = true
        submitButton.setOnClickListener {
            // No city was entered
            if (cityEditText.length() < 3) {
                val failedString = getString(R.string.city_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No state was entered
            if (stateSpinner.selectedItemPosition == 0) {
                val failedString = getString(R.string.state_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // ZIP code is not entered correctly
            if (zipCodeEditText.length() != 5) {
                val failedString = getString(R.string.zip_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No home address was entered
            if (homeAddressEditText.length() < 5) {
                val failedString = getString(R.string.home_address_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No work or school address was entered
            if (workSchoolAddressEditText.length() < 5) {
                val failedString = getString(R.string.work_school_address_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // Data is valid, open up Home Activity
            if (initialDataIsValid) {
                val intent: Intent = if (initialSetup) {
                    Intent(this, ICalActivity::class.java)
                } else {
                    Intent(this, HomeActivity::class.java)
                }

                putLocationInformation(intent)
            }

            initialDataIsValid = true
        }
    }

    private fun putLocationInformation(intent: Intent) {
        val city = cityEditText.text
        val statePos = stateSpinner.selectedItemPosition
        val state = stateCodes[statePos]
        val zip = zipCodeEditText.text

        lateinit var temperature: String
        if (fahrenheit.isChecked) { temperature = "imperial" }
        if (celsius.isChecked) { temperature = "celsius" }
        if (kelvin.isChecked) { temperature = "kelvin" }

        val homeAddress = homeAddressEditText.text
        val workSchoolAddress = workSchoolAddressEditText.text
        val url = getString(R.string.server_url) +
            "location?city=$city&state=$state&zip=$zip&home=$homeAddress&work=$workSchoolAddress&units=$temperature"
        putRequest(this, intent, url, "location")
    }
}
