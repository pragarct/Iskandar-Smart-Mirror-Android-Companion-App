package com.iskandar.mirror.companion.app.activities.ui

import android.graphics.Color
import android.graphics.Color.alpha
import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_lighting.*

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

        // Submit button - when pressed, ?
        submitButton.setOnClickListener {
            submitButton.text = String.format("#%06X", 0xFFFFFF and colorPickerView.selectedColor)
            val alpha: Int = alpha(colorPickerView.selectedColor)
            alphaSliderText.text = (alpha / 2.55).toInt().toString()
        }

        // This will be the color the mirror had previously
        resetButton.setBackgroundColor(colorPickerView.selectedColor)
        resetButton.setTextColor(getComplementaryColor(colorPickerView.selectedColor))
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
