package com.example.mytodoapp.testhelpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withParentIndex
import com.google.android.material.tabs.TabLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf

/**
 * @param parentViewId the resource id of the parent [View].
 * @param position the child index of the [View] to match.
 * @return a [Matcher] that matches the child [View] which has the given [position] within the specified parent.
 */
fun withPositionInParent(parentViewId: Int, position: Int): Matcher<View> {
    return allOf(withParent(withId(parentViewId)), withParentIndex(position))
}

fun selectTabAtPosition(tabIndex: Int, findWithoutSelect: Boolean = false): ViewAction {
    return object : ViewAction {
        override fun getDescription() = "with tab at index $tabIndex"

        override fun getConstraints() =
            allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

        override fun perform(uiController: UiController, view: View) {
            val tabLayout = view as TabLayout
            val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                ?: throw PerformException.Builder()
                    .withCause(Throwable("No tab at index $tabIndex"))
                    .build()

            if (!findWithoutSelect) tabAtIndex.select()
        }
    }
}

fun withActionIconDrawable(@DrawableRes resourceId: Int): Matcher<View?> {
    return object : BoundedMatcher<View?, ActionMenuItemView>(ActionMenuItemView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has image drawable resource $resourceId")
        }

        override fun matchesSafely(actionMenuItemView: ActionMenuItemView): Boolean {
            return sameBitmap(
                actionMenuItemView.context,
                actionMenuItemView.itemData.icon,
                resourceId
            )
        }
    }
}

private fun sameBitmap(
    context: Context,
    drawable: Drawable?,
    resourceId: Int,
): Boolean {
    val otherDrawable: Drawable? = context.resources.getDrawable(resourceId, context.theme)
    if (drawable == null || otherDrawable == null) {
        return false
    }

    val bitmap = getBitmapFromDrawable(drawable)
    val otherBitmap = getBitmapFromDrawable(otherDrawable)
    return bitmap.sameAs(otherBitmap)
}

private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
    val bitmap: Bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}