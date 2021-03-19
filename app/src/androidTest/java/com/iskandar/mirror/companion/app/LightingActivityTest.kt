package com.iskandar.mirror.companion.app

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.slider.AlphaSlider
import com.flask.colorpicker.slider.LightnessSlider
import com.iskandar.mirror.companion.app.activities.ui.LightingActivity
import com.iskandar.mirror.companion.app.classes.lazyActivityScenarioRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class LightingActivityTest {

    @get:Rule
    val rule = lazyActivityScenarioRule<LightingActivity>(launchActivity = false)

    @Test
    fun incorrectCity_toastErrorMessageIsDisplayed() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), LightingActivity::class.java)
        val red = 97; val green = 54; val blue = 147; val brightness = 0.4
        var hex = java.lang.String.format("#%02x%02x%02x", red, green, blue)
        hex = hex.toUpperCase(Locale.ROOT)
        intent.putExtra("color", hex)
        intent.putExtra("brightness", brightness.toString())
        intent.putExtra("key", "value") // obviously use a const for key
        rule.launch(intent)

        onView(withId(R.id.colorPickerView)).perform(setValue(Color.parseColor(hex)))
        onView(withId(R.id.lightnessSlider)).perform(setLightnessValue(Color.parseColor(hex)))
        onView(withId(R.id.colorPickerView)).perform(setAlphaValue(brightness.toFloat()))
        // onView(withId(R.id.colorPickerView)).perform(setAlphaSlider(withId(R.id.alphaSlider)))
        onView(withId(R.id.hexCodeEditText)).perform(typeText(hex), closeSoftKeyboard())

        onView(withId(R.id.colorPickerView)).check(matches(withValue(Color.parseColor(hex))))
        // onView(withId(R.id.lightnessSlider)).check(matches(withLightnessValue(Color.parseColor(hex))))
        // this will fail unless setAlphaSlider is called?
        // onView(withId(R.id.colorPickerView)).check(matches(withAlphaValue(brightness.toFloat())))

        onView(withId(R.id.submitButton)).perform(click())
        onView(withText(R.string.keep_color_confirm)).check(matches(isDisplayed()))
        // onView(withId(android.R.id.button1)).perform(click())
        onView(withId(android.R.id.button2)).perform(click())
    }

    /*private fun setDefaultValues() {
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
    }*/

    /*@Test
    fun correctInformation_noToastErrorIsDisplayed() {
        *//*onView(withId(R.id.cityEditText)).perform(typeText("Amo"), closeSoftKeyboard())
        onView(withId(R.id.stateSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ohio"))).perform(click())
        onView(withId(R.id.stateSpinner)).check(matches(withSpinnerText(containsString("Ohio"))))
        onView(withId(R.id.zipCodeEditText)).perform(typeText("11111"), closeSoftKeyboard())
        onView(withId(R.id.celsius)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123 Main Street"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456 Second Street"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.state_error_message).check(doesNotExist())
        onToast(R.string.city_error_message).check(doesNotExist())
        onToast(R.string.zip_error_message).check(doesNotExist())
        onToast(R.string.home_address_error_message).check(doesNotExist())
        onToast(R.string.work_school_address_error_message).check(doesNotExist())
        onToast(R.string.rest_put_error).check(doesNotExist())*//*
    }*/

    private fun withValue(expectedValue: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, ColorPickerView>(ColorPickerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("expected: $expectedValue")
            }

            override fun matchesSafely(slider: ColorPickerView?): Boolean {
                return slider?.selectedColor == expectedValue
            }
        }
    }

    private fun setValue(value: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set Slider value to $value"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(ColorPickerView::class.java)
            }

            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as ColorPickerView
                seekBar.setColor(value, false)
            }
        }
    }

    private fun withLightnessValue(expectedValue: Float): Matcher<View?> {
        return object : BoundedMatcher<View?, LightnessSlider>(LightnessSlider::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("expected: $expectedValue")
            }

            override fun matchesSafely(slider: LightnessSlider?): Boolean {
                return slider?.alpha == expectedValue
            }
        }
    }

    private fun setLightnessValue(value: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set Slider value to $value"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(LightnessSlider::class.java)
            }

            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as LightnessSlider
                seekBar.setColor(value)
            }
        }
    }

    private fun withAlphaValue(expectedValue: Float): Matcher<View?> {
        return object : BoundedMatcher<View?, ColorPickerView>(ColorPickerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("expected: $expectedValue")
            }

            override fun matchesSafely(slider: ColorPickerView?): Boolean {
                return slider?.alpha == expectedValue
            }
        }
    }

    private fun setAlphaValue(value: Float): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set Slider value to $value"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(ColorPickerView::class.java)
            }

            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as ColorPickerView
                seekBar.setAlphaValue(value)
                // seekBar.alpha = value
                // val colorPickerView: ColorPickerView = rule.getScenario()
                // colorPickerView.setAlphaSlider(seekBar)
            }
        }
    }

    private fun setAlphaSlider(alphaSlider: AlphaSlider): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set Slider value to $alphaSlider"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(AlphaSlider::class.java)
            }

            override fun perform(uiController: UiController?, view: View) {
                val colorPickerView = view as ColorPickerView
                colorPickerView.setAlphaSlider(alphaSlider)
            }
        }
    }
}
