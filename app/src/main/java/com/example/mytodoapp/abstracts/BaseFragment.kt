package com.example.mytodoapp.abstracts

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

    companion object {
        const val TRANSITION_DURATION = 300L
        const val TRANSITION_ELEMENT_ROOT = "transition:root:"
    }
}