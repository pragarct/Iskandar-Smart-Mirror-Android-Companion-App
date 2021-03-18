package com.iskandar.mirror.companion.app

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iskandar.mirror.companion.app.activities.ui.LocationActivity
import com.iskandar.mirror.companion.app.classes.ToastMatcher.Companion.onToast
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<LocationActivity> = ActivityScenarioRule(LocationActivity::class.java)

    @Test
    fun incorrectCity_toastErrorMessageIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("A"), closeSoftKeyboard())
        onView(withId(R.id.stateSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ohio"))).perform(click())
        onView(withId(R.id.stateSpinner)).check(matches(withSpinnerText(containsString("Ohio"))))
        onView(withId(R.id.zipCodeEditText)).perform(typeText("11111"), closeSoftKeyboard())
        onView(withId(R.id.kelvin)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123 Main Street"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456 Second Street"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.city_error_message).check(matches(isDisplayed()))
    }

    @Test
    fun incorrectState_toastErrorMessageIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("A"), closeSoftKeyboard())
        onView(withId(R.id.zipCodeEditText)).perform(typeText("11111"), closeSoftKeyboard())
        onView(withId(R.id.kelvin)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123 Main Street"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456 Second Street"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.city_error_message).check(matches(isDisplayed()))
    }

    @Test
    fun incorrectZip_toastErrorMessageIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("Amo"), closeSoftKeyboard())
        onView(withId(R.id.stateSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ohio"))).perform(click())
        onView(withId(R.id.stateSpinner)).check(matches(withSpinnerText(containsString("Ohio"))))
        onView(withId(R.id.zipCodeEditText)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.kelvin)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123 Main Street"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456 Second Street"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.zip_error_message).check(matches(isDisplayed()))
    }

    @Test
    fun incorrectHomeAddress_toastErrorMessageIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("Amo"), closeSoftKeyboard())
        onView(withId(R.id.stateSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ohio"))).perform(click())
        onView(withId(R.id.stateSpinner)).check(matches(withSpinnerText(containsString("Ohio"))))
        onView(withId(R.id.zipCodeEditText)).perform(typeText("11111"), closeSoftKeyboard())
        onView(withId(R.id.kelvin)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456 Second Street"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.home_address_error_message).check(matches(isDisplayed()))
    }

    @Test
    fun incorrectWorkSchoolAddress_toastErrorMessageIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("Amo"), closeSoftKeyboard())
        onView(withId(R.id.stateSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Ohio"))).perform(click())
        onView(withId(R.id.stateSpinner)).check(matches(withSpinnerText(containsString("Ohio"))))
        onView(withId(R.id.zipCodeEditText)).perform(typeText("11111"), closeSoftKeyboard())
        onView(withId(R.id.kelvin)).perform(click())

        onView(withId(R.id.homeAddressEditText)).perform(typeText("123 Main Street"), closeSoftKeyboard())
        onView(withId(R.id.workSchoolAddressEditText)).perform(typeText("456"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        onToast(R.string.work_school_address_error_message).check(matches(isDisplayed()))
    }

    @Test
    fun correctInformation_noToastErrorIsDisplayed() {
        onView(withId(R.id.cityEditText)).perform(typeText("Amo"), closeSoftKeyboard())
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
        onToast(R.string.rest_put_error).check(doesNotExist())
    }
}
