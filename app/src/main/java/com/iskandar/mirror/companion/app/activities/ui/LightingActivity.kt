package com.iskandar.mirror.companion.app.activities.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.alpha
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_lighting.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.regex.Matcher
import java.util.regex.Pattern

var previousColor: String = "#FFFFFF"
var previousBrightness: Float = 100f
var justStarted = true

class LightingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lighting)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_lighting)
        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(hexCodeEditText)
        hexCodeEditText.onRightDrawableClicked { it.text.clear() }
        hexCodeEditText.makeClearableEditText(null, null)

        // Update hex edit text when color picker is changed
        colorPickerView.addOnColorChangedListener {
            val color = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
            val buttonColor = "#$color"
            // Change submit button background to selected color
            submitButton.backgroundTintList = generateColorStateList(Color.parseColor(buttonColor))

            // Change submit button text to complimentary color
            submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
            // Change hex edit text to new color selected
            hexCodeEditText.setText(color)
        }

        // Update manual button to inputted color in hex EditText
        hexCodeEditText.addTextChangedListener {
            if (hexCodeEditText.text.length == 6 && hexCodeEditText.hasFocus()) {
                val color = "#" + hexCodeEditText.text.toString()

                if (isHexColorCode(color)) {
                    // Change manual button background to selected color
                    manualButton.backgroundTintList = generateColorStateList(
                        Color.parseColor(color)
                    )
                    // Change manual button text to complimentary color
                    manualButton.setTextColor(getComplementaryColor(Color.parseColor(color)))
                    hexCodeEditText.requestFocus()
                }
            }
        }

        // Manual button, when pressed, change color to manually inputted hex value
        manualButton.setOnClickListener {
            val color = "#" + hexCodeEditText.text.toString()

            // Set background to transparent
            manualButton.backgroundTintList = generateColorStateList(Color.LTGRAY)
            manualButton.setTextColor(Color.BLACK)

            if (isHexColorCode(color)) {
                colorPickerView.setColor(Color.parseColor(color), false)
                // Change submit button background to selected color
                submitButton.backgroundTintList = generateColorStateList(Color.parseColor(color))
                // Change submit button text to complimentary color
                submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
            } else {
                val failedString = getString(R.string.hex_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
            }
        }

        // Submit button - when pressed, a popup appears asking to confirm the color
        submitButton.setOnClickListener {
            putLightingInformation(this)
        }

        // Get the previous/current color from intent
        previousColor = intent.extras?.getString("color").toString()
        // Change reset button background to selected color
        resetButton.backgroundTintList = generateColorStateList(Color.parseColor(previousColor))
        // Change reset button text to complimentary color
        resetButton.setTextColor(getComplementaryColor(Color.parseColor(previousColor)))

        // Get the previous/current brightness from intent
        val brightnessString: String = "0" + intent.extras?.getString("brightness")
        previousBrightness = brightnessString.toFloat()

        // Reset button - when pressed, change color to previously selected color
        resetButton.setOnClickListener {
            setDefaultValues()
            // Change submit button background to selected color
            submitButton.backgroundTintList = generateColorStateList(colorPickerView.selectedColor)
            // Change submit button text to complimentary color
            submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
        }

        // This is necessary, otherwise the brightness (alpha) slider is not initialized
        drawer_layout.viewTreeObserver.addOnGlobalLayoutListener {
            if (justStarted) {
                setDefaultValues()
                justStarted = false
            }
        }
    }

    private fun setDefaultValues() {
        // Set the current color to the one previously selected
        colorPickerView.setColor(Color.parseColor(previousColor), false)
        // Set the lightness to the one previously selected
        lightnessSlider.setColor(Color.parseColor(previousColor))
        // Set the current brightness to the one previously selected
        colorPickerView.setAlphaValue(previousBrightness)
        colorPickerView.setAlphaSlider(alphaSlider)

        // Change hex edit text to previous color selected
        val color = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
        hexCodeEditText.setText(color)
    }

    private fun putLightingInformation(context: Context) {
        // val context = this

        doAsync {
            // Convert to hex
            val hexColor = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
            // val color = "#$hexColor"
            // val color = Color.parseColor("#$hexColor")
            val brightness = (colorPickerView.selectedColor.alpha / 2.55).toInt()

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val url = "http://10.0.2.2:5000/lighting?rgb=$hexColor&brightness=$brightness"

            val future = RequestFuture.newFuture<String>()
            val stringRequest = StringRequest(Request.Method.PUT, url, future, future)
            // Add the request to the RequestQueue.
            queue.add(stringRequest)

            try {
                val response = future.get(5000, TimeUnit.MILLISECONDS) // this will block
                Log.d("Response", response)

                // build alert dialog
                runOnUiThread {
                    val dialogBuilder = AlertDialog.Builder(context)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Do you want to keep this color?")
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("Keep Color") { _, _ ->
                            val intent = Intent(context, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        // negative button text and action
                        .setNegativeButton("Choose New Color") { dialog, _ -> dialog.cancel() }

                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Color Confirmation")
                    // show alert dialog
                    alert.show()
                }
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
                // Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
            } catch (e: ExecutionException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
            } catch (e: TimeoutException) {
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.rest_put_error), Toast.LENGTH_LONG).show()
                }
                Log.d("Response", "LIGHTING TIMEOUT")
            }
        }
    }

    private fun isHexColorCode(color: String): Boolean {
        // Regex to confirm EditText contains a hex code
        val colorPattern: Pattern = Pattern.compile("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
        val m: Matcher = colorPattern.matcher(color)
        return m.matches()
    }

    // method to generate color state list programmatically
    private fun generateColorStateList(color: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_active),
            intArrayOf(-android.R.attr.state_active),
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused)
        )
        val colors = intArrayOf(
            color, // enabled
            color, // disabled
            color, // checked
            color, // unchecked
            color, // active
            color, // inactive
            color, // pressed
            color // focused
        )
        return ColorStateList(states, colors)
    }

    // Used to set text color so it is visible
    private fun getComplementaryColor(color: Int): Int {
        var r = color and 255
        var g = color shr 8 and 255
        var b = color shr 16 and 255
        val a = color shr 24 and 255
        r = 255 - r
        g = 255 - g
        b = 255 - b
        return r + (g shl 8) + (b shl 16) + (a shl 24)
    }
}
