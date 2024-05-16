package com.example.mytodoapp.components.abstracts

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.mytodoapp.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Material fragments transition animation
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialFadeThrough()
    }

    /**
     * @param view the root view of the fragment
     */
    protected fun hideKeyboardFromCurrentFocus(view: View) {
        if (view is ViewGroup)
            findCurrentFocus(view)
    }

    /**
     * Check if any of its children has focus then
     * hide the keyboard
     */
    private fun findCurrentFocus(viewGroup: ViewGroup) {
        viewGroup.children.forEach {
            if (it is ViewGroup)
            // If the current children is an instance of
            // a ViewGroup, then iterate its children too.
                findCurrentFocus(it)
            else {
                if (it.hasFocus()) {
                    hideKeyboardFromView(it)
                    return
                }
            }
        }
    }

    /**
     * Try to hide the soft keyboard with the view's
     * window token
     *
     * @param view the view which has the focus
     */
    private fun hideKeyboardFromView(view: View) {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Custom Material transitions animation into the container (fragment)
     * for navigation between fragments
     *
     * @param id - drawing View Id (navigationHostFragment)
     */
    fun baseContainerTransform(@IdRes id: Int = R.id.nav_host_fragment) =
        MaterialContainerTransform().apply {
            drawingViewId = id
            duration = TRANSITION_DURATION
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            interpolator = FastOutSlowInInterpolator()
            setAllContainerColors(
                MaterialColors.getColor(
                    requireContext(), com.google.android.material.R.attr.colorSurface,
                    ContextCompat.getColor(requireContext(), androidx.appcompat.R.color.background_material_dark)
                )
            )
        }

    companion object {
        const val TRANSITION_DURATION = 300L
        const val TRANSITION_ELEMENT_ROOT = "transition:root:"
    }
}