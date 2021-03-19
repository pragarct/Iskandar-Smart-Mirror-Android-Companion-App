package com.iskandar.mirror.companion.app.activities.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.core.graphics.alpha
import androidx.core.widget.addTextChangedListener
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_lighting.*
import java.util.regex.Matcher
import java.util.regex.Pattern

var previousColorInt: Int = 0
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
        // Reset variable
        justStarted = true

        // Update hex edit text when color picker is changed
        colorPickerView.addOnColorChangedListener {
            // Change submit button background to selected color
            submitButton.backgroundTintList = generateColorStateList(colorPickerView.selectedColor)
            // Change submit button text to complimentary color
            if ((colorPickerView.selectedColor.alpha / 2.55).toInt() > 50) {
                submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
            } else {
                val blackHex = "#000000"
                submitButton.setTextColor(Color.parseColor(blackHex))
            }
            // Change hex edit text to new color selected
            val color = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
            hexCodeEditText.setText(color)
        }

        // Update manual button to inputted color in hex EditText
        hexCodeEditText.addTextChangedListener {
            if (hexCodeEditText.text.length == 6 && hexCodeEditText.hasFocus()) {
                val hex = "#" + hexCodeEditText.text.toString()

                if (isHexColorCode(hex)) {
                    // Change manual button background to selected color
                    manualButton.backgroundTintList = generateColorStateList(Color.parseColor(hex))
                    // Change manual button text to complimentary color
                    if ((colorPickerView.selectedColor.alpha / 2.55).toInt() > 50) {
                        manualButton.setTextColor(getComplementaryColor(Color.parseColor(hex)))
                    } else {
                        val blackHex = "#000000"
                        manualButton.setTextColor(Color.parseColor(blackHex))
                    }
                }
            }
        }

        // Manual button, when pressed, change color to manually inputted hex value
        manualButton.setOnClickListener {
            // Set background to transparent
            manualButton.backgroundTintList = generateColorStateList(Color.LTGRAY)
            manualButton.setTextColor(Color.BLACK)

            val hex = "#" + hexCodeEditText.text.toString()
            if (isHexColorCode(hex)) {
                // Set the current color to the one previously selected
                colorPickerView.setColor(Color.parseColor(hex), false)
                // Change submit button background to selected color
                submitButton.backgroundTintList = generateColorStateList(colorPickerView.selectedColor)
                // Change submit button text to complimentary color
                submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
            } else {
                val failedString = getString(R.string.hex_error_message)
                Toast.makeText(this, failedString, Toast.LENGTH_LONG).show()
            }
        }

        // Submit button - when pressed, a popup appears asking to confirm the color
        submitButton.setOnClickListener {
            putLightingInformation()
        }

        // Reset button - when pressed, change color to previously selected color
        resetButton.setOnClickListener {
            setDefaultValues()
            // Change submit button background to selected color
            submitButton.backgroundTintList = generateColorStateList(colorPickerView.selectedColor)
            // Change submit button text to complimentary color
            if ((colorPickerView.selectedColor.alpha / 2.55).toInt() > 50) {
                submitButton.setTextColor(getComplementaryColor(Color.parseColor(previousColor)))
            } else {
                val blackHex = "#000000"
                submitButton.setTextColor(Color.parseColor(blackHex))
            }
        }

        // This is necessary, otherwise the brightness (alpha) slider is not initialized
        drawer_layout.viewTreeObserver.addOnGlobalLayoutListener {
            if (justStarted) {
                // Get the previous/current color from intent
                previousColor = intent.extras?.getString("color").toString()
                // Get the previous/current brightness from intent
                val brightnessString: String = "0" + intent.extras?.getString("brightness")
                previousBrightness = brightnessString.toFloat()
                previousColorInt = colorPickerView.selectedColor

                setDefaultValues()

                // Change reset button background to selected color
                resetButton.backgroundTintList = generateColorStateList(colorPickerView.selectedColor)
                // Change reset button text to complimentary color
                if ((colorPickerView.selectedColor.alpha / 2.55).toInt() > 50) {
                    resetButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
                } else {
                    val blackHex = "#000000"
                    resetButton.setTextColor(Color.parseColor(blackHex))
                }

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

    private fun putLightingInformation() {
        val intent = Intent(this, HomeActivity::class.java)
        // Convert to hex
        val hexColor = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
        val brightness = (colorPickerView.selectedColor.alpha / 2.55).toInt()
        val url = getString(R.string.server_url) + "lighting?rgb=$hexColor&brightness=$brightness"
        putRequest(this, intent, url, "lighting", false)
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
        // get existing colors
        val alpha = Color.alpha(color)
        var red = Color.red(color)
        var blue = Color.blue(color)
        var green = Color.green(color)

        // find compliments
        red = red.inv() and 0xff
        blue = blue.inv() and 0xff
        green = green.inv() and 0xff
        return Color.argb(alpha, red, green, blue)
    }
}
