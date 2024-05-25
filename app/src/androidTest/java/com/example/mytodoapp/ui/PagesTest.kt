package com.example.mytodoapp.ui

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodoapp.R
import com.example.mytodoapp.features.ui.AllPagesFragment
import com.example.mytodoapp.testhelpers.DisableAnimationsRule
import com.example.mytodoapp.testhelpers.hilt.launchFragmentInHiltContainer
import com.example.mytodoapp.testhelpers.selectTabAtPosition
import com.example.mytodoapp.testhelpers.withPositionInParent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PagesTest {

    private lateinit var allPagesFragment: AllPagesFragment

    // Enable Hilt for the test class using HiltAndroidRule
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val disableAnimationsRule = DisableAnimationsRule()

    @Before
    fun launchCurrentFragment() {
        launchFragmentInHiltContainer<AllPagesFragment> {
            allPagesFragment = this as AllPagesFragment
        }
    }

    @Test
    fun testNavigationToEditTaskFragment() {
        // Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Use custom launchFragmentInContainer function for Hilt
        launchFragmentInHiltContainer<AllPagesFragment> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withPositionInParent(R.id.item_tasks_recycler, 0)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.edit_task_fragment)
    }

    @Test
    fun testShowingCreateTaskBottomSheet() {
        onView(withId(R.id.add_task_floating_action_button)).perform(click())
        onView(withId(R.id.task_create_bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowingCreateGroupBottomSheet() {
        // Index hardcode!
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(2, true)).perform(click())
        onView(withId(R.id.group_create_bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun testSetTaskFinishedAndVerse() {
        // Indexes hardcode!
        var viewInteraction = onView(
            allOf(
                withId(R.id.task_ready_checkBox),
                withParent(withPositionInParent(R.id.item_tasks_recycler, 0))
            )
        )
        viewInteraction.check(matches(isNotChecked()))

        viewInteraction.perform(click())
        viewInteraction = onView(
            allOf(
                withId(R.id.task_ready_checkBox),
                withParent(withPositionInParent(R.id.item_tasks_recycler, 2))
            )
        )
        viewInteraction.check(matches(isChecked()))

        viewInteraction.perform(click())
        viewInteraction = onView(
            allOf(
                withId(R.id.task_ready_checkBox),
                withParent(withPositionInParent(R.id.item_tasks_recycler, 0))
            )
        )
        viewInteraction.check(matches(isNotChecked()))
    }

    @Test
    fun testSetTaskStaredAndVerse() {
        // First unchecked hardcode!
        val viewInteraction = onView(
            allOf(
                withId(R.id.task_stared_checkBox),
                withParent(withPositionInParent(R.id.item_tasks_recycler, 0))
            )
        )
        viewInteraction.check(matches(isNotChecked()))

        viewInteraction.perform(click())
        viewInteraction.check(matches(isChecked()))

        viewInteraction.perform(click())
        viewInteraction.check(matches(isNotChecked()))
    }

    @Test
    fun testSetStaredAndShowInStaredGroupAndVerse() {
        // First unchecked hardcode!
        val viewInteraction = onView(
            allOf(
                withId(R.id.task_stared_checkBox),
                withParent(withPositionInParent(R.id.item_tasks_recycler, 0))
            )
        )
        viewInteraction.check(matches(isNotChecked()))

        viewInteraction.perform(click())
        viewInteraction.check(matches(isChecked()))

        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))

        ViewActions.swipeLeft()
    }
}