package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_change_location.*
import kotlinx.android.synthetic.main.activity_change_location.nav_view
import kotlinx.android.synthetic.main.activity_change_location.submitButton
import org.jetbrains.anko.doAsync
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ChangeLocationActivity : BaseActivity() {

    private var initialSetup = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_location)

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
        val homeAddress: String? = intent.extras?.getString("homeAddress")
        val workSchoolAddress: String? = intent.extras?.getString("workSchoolAddress")

        if (!zipCode.isNullOrEmpty() && !city.isNullOrEmpty() &&
            !homeAddress.isNullOrEmpty() && !workSchoolAddress.isNullOrEmpty()
        ) {
            // Set edit texts to currently set values
            cityEditText.setText(city)
            stateEditText.setText(state)
            zipCodeEditText.setText(zipCode)
            homeAddressEditText.setText(homeAddress)
            workSchoolAddressEditText.setText(workSchoolAddress)
        }
    }

    private fun setupListenersAndEditTexts() {
        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(cityEditText)
        cityEditText.onRightDrawableClicked { it.text.clear() }
        cityEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(stateEditText)
        stateEditText.onRightDrawableClicked { it.text.clear() }
        stateEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(zipCodeEditText)
        zipCodeEditText.onRightDrawableClicked { it.text.clear() }
        zipCodeEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(homeAddressEditText)
        homeAddressEditText.onRightDrawableClicked { it.text.clear() }
        homeAddressEditText.makeClearableEditText(null, null)
        addRightCancelDrawable(workSchoolAddressEditText)
        workSchoolAddressEditText.onRightDrawableClicked { it.text.clear() }
        workSchoolAddressEditText.makeClearableEditText(null, null)

        // Submit is pressed, run data validation
        var initialDataIsValid = true
        submitButton.setOnClickListener {
            // No city was entered
            if (cityEditText.length() == 0) {
                val failedString = getString(R.string.city_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
                initialDataIsValid = false
            }

            // No state was entered
            if (stateEditText.length() == 0) {
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
                val intent: Intent = if (initialSetup) {
                    Intent(this, ChangeGmailActivity::class.java)
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
        val state = stateEditText.text
        val zip = zipCodeEditText.text
        val homeAddress = homeAddressEditText.text
        val workSchoolAddress = workSchoolAddressEditText.text
        val url = getString(R.string.server_url) +
            "location?city=$city&state=$state&zip=$zip&home=$homeAddress&work=$workSchoolAddress"
        putRequest(this, intent, url, "location")
    }

    private fun putWeatherAndTrafficInformation(intent: Intent) {
        val context = this

        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val weatherUrl = getString(R.string.server_url) + "weather?city=" +
                cityEditText.text.toString() + "&zip_code=" +
                zipCodeEditText.text.toString()

            val future1 = RequestFuture.newFuture<String>()
            val weatherStringRequest = StringRequest(Request.Method.PUT, weatherUrl, future1, future1)
            // Add the request to the RequestQueue.
            queue.add(weatherStringRequest)

            try {
                val response = future1.get(500, TimeUnit.MILLISECONDS) // this will block
                Log.d("Response", response)
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: ExecutionException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: TimeoutException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
                Log.d("Response", "WEATHER TIMEOUT")
            }

            val trafficUrl = getString(R.string.server_url) + "traffic?home_address=" +
                homeAddressEditText.text.toString() + "&work_or_school_address=" +
                workSchoolAddressEditText.text.toString()

            val future2 = RequestFuture.newFuture<String>()
            val trafficStringRequest = StringRequest(Request.Method.PUT, trafficUrl, future2, future2)
            // Add the request to the RequestQueue.
            queue.add(trafficStringRequest)

            try {
                val response = future2.get(500, TimeUnit.MILLISECONDS) // this will block
                Log.d("Response", response)
                startActivity(intent)
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: ExecutionException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: TimeoutException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
                Log.d("Response", "TRAFFIC TIMEOUT")
            }
        }
    }
}
