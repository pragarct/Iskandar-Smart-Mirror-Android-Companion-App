package com.iskandar.mirror.companion.app.activities.ui

import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.InformationBaseActivity
import kotlinx.android.synthetic.main.activity_change_location.*

class ChangeLocationActivity : InformationBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_location)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_location)
        // This is a method in InformationBaseActivity used also in InitialSetupActivity
        setupListenersAndEditTexts()

        val zipCode: String? = intent.extras?.getString("zipCode")
        val city: String? = intent.extras?.getString("city")
        val homeAddress: String? = intent.extras?.getString("homeAddress")
        val workSchoolAddress: String? = intent.extras?.getString("workSchoolAddress")

        if (!zipCode.isNullOrEmpty() && !city.isNullOrEmpty() &&
            !homeAddress.isNullOrEmpty() && !workSchoolAddress.isNullOrEmpty()
        ) {
            // Set edit texts to currently set values
            zipCodeEditText.setText(zipCode)
            cityEditText.setText(city)
            homeAddressEditText.setText(homeAddress)
            workSchoolAddressEditText.setText(workSchoolAddress)
        }
    }
}
