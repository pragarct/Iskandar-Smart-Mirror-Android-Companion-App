package com.iskandar.mirror.companion.app.classes

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.activities.ui.HomeActivity
import kotlinx.android.synthetic.main.activity_initial_setup.cityEditText
import kotlinx.android.synthetic.main.activity_initial_setup.homeAddressEditText
import kotlinx.android.synthetic.main.activity_initial_setup.submitButton
import kotlinx.android.synthetic.main.activity_initial_setup.workSchoolAddressEditText
import kotlinx.android.synthetic.main.activity_initial_setup.zipCodeEditText
import org.jetbrains.anko.doAsync
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
                putWeatherAndTrafficInformation(intent)
            }

            initialDataIsValid = true
        }
    }

    private fun putWeatherAndTrafficInformation(intent: Intent) {
        val context = this

        doAsync {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val weatherUrl = "http://10.0.2.2:5000/weather?city=" +
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

            val trafficUrl = "http://10.0.2.2:5000/traffic?home_address=" +
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
