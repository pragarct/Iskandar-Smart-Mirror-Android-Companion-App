package com.iskandar.mirror.companion.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iskandar.mirror.companion.app.activities.ui.OldMainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OldMainActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<OldMainActivity> = ActivityScenarioRule(OldMainActivity::class.java)

    @Test
    fun typeANumber_resultIsDisplayed() {
        onView(withId(R.id.edit_text_factorial)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.button_compute)).perform(click())

        onView(withId(R.id.text_result)).check(matches(isDisplayed()))
        onView(withId(R.id.text_result)).check(matches(withText("1")))
    }
}