package com.example.mytodoapp.ui

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodoapp.R
import com.example.mytodoapp.features.ui.edittask.EditTaskFragment
import com.example.mytodoapp.testhelpers.DisableAnimationsRule
import com.example.mytodoapp.testhelpers.hilt.launchFragmentInHiltContainer
import com.example.mytodoapp.testhelpers.withActionIconDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditTaskFragmentTest {

    private lateinit var editTaskFragment: EditTaskFragment

    // Enable Hilt for the test class using HiltAndroidRule
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val disableAnimationsRule = DisableAnimationsRule()

    @Before
    fun launchCurrentFragment() {
        launchFragmentInHiltContainer<EditTaskFragment> {
            editTaskFragment = this as EditTaskFragment
        }
    }

    @Test
    fun testBackToAllPagesFragmentWithPressBack() {
        // Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Use custom launchFragmentInContainer function for Hilt
        launchFragmentInHiltContainer<EditTaskFragment> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        ViewActions.pressBack()
        assertEquals(navController.currentDestination?.id, R.id.all_pages_fragment)
    }

    @Test
    fun testClickStaredButton() {
        //turn off animation on your device before
        with(onView(withId(R.id.stared))) {
            check(matches(withActionIconDrawable(R.drawable.ic_star_outline_24)))
            perform(click())
            check(matches(withActionIconDrawable(R.drawable.ic_star_fill_24)))
        }
    }

    @Test
    fun testClickDeleteButton() {
        // Text hardcode!
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText("Delete")).perform(click())

        onView(withText("Cancel")).check(matches(isDisplayed()))
    }

    @Test
    fun testClickChooseGroupButton() {
        onView(withId(R.id.choose_task_group_button)).perform(click())
        onView(withId(R.id.choose_group_bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun testTitleAndDetailsEditTextWriting() {
        with(onView(withId(R.id.edit_task_title_editText))) {
            check(matches(withText("")))
            perform(replaceText("New Title"))
            check(matches(withText("New Title")))
        }

        with(onView(withId(R.id.edit_task_details_editText))) {
            check(matches(withText("")))
            perform(replaceText("New Details"))
            check(matches(withText("New Details")))
        }

    }

    @Test
    fun testShowDateTimePickerDialog() {
        with(onView(withId(R.id.edit_dateTime_button))) {
            check(matches(withText("Set date/time")))
            perform(click())
        }
        onView(withId(R.id.date_time_picker_dialog)).check(matches(isDisplayed()))
    }

    @Test
    fun testSetFinishedButton() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Use custom launchFragmentInContainer function for Hilt
        launchFragmentInHiltContainer<EditTaskFragment> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(requireView(), navController)
        }

        with(onView(withId(R.id.ready_task_floating_action_button))) {
            check(matches(withText("Task ready")))
            perform(click())
        }

        assertEquals(navController.currentDestination?.id, R.id.all_pages_fragment)
    }
}