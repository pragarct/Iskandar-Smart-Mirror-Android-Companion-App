package com.iskandar.mirror.companion.app.activities.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_lighting.*

var previousColor: String = "#FFFFFF"

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
            // Change submit button background to selected color
            submitButton.setBackgroundColor(colorPickerView.selectedColor)
            // Change submit button text to complimentary color
            submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
            // Change hex edit text to new color selected
            val color = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
            hexCodeEditText.setText(color)
        }

        // Manual button, when pressed, change color to manually inputted hex value
        manualButton.setOnClickListener {
            val color = "#" + hexCodeEditText.text.toString()
            colorPickerView.setColor(Color.parseColor(color), false)
        }

        // Submit button - when pressed, a popup appears asking to confirm the color
        submitButton.setOnClickListener {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Do you want to keep this color?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Keep Color") { _, _ -> finish() }
                // negative button text and action
                .setNegativeButton("Choose New Color") { dialog, _ -> dialog.cancel() }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Color Confirmation")
            // show alert dialog
            alert.show()
        }

        // Get the previous/current color from intent
        previousColor = intent.extras?.getString("color").toString()
        // Change reset button background to selected color
        resetButton.setBackgroundColor(Color.parseColor(previousColor))
        // Change reset button text to complimentary color
        resetButton.setTextColor(getComplementaryColor(Color.parseColor(previousColor)))

        // Set the current color to the one previously selected
        lightnessSlider.setColor(Color.parseColor(previousColor))

        // Reset button - when pressed, change color to previously selected color
        resetButton.setOnClickListener {
            colorPickerView.setColor(Color.parseColor(previousColor), false)

            // Change hex edit text to previous color selected
            val color = String.format("%06X", 0xFFFFFF and colorPickerView.selectedColor)
            hexCodeEditText.setText(color)

            // Change submit button background to selected color
            submitButton.setBackgroundColor(colorPickerView.selectedColor)
            // Change submit button text to complimentary color
            submitButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
        }

        // Retrieve initial color
        val color: String? = intent.extras?.getString("color")
        colorPickerView.setColor(Color.parseColor(color), false)
    }

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
