package com.example.mytodoapp.extensions

import android.graphics.Paint
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 *  Extension function used to set an strike through effect on the
 *  painted text on the view
 *
 *  @param isStrikeThrough determines whether to add or remove the effect on the text
 */
fun TextView.setStrikeThroughEffect(isStrikeThrough: Boolean) {
    if (isStrikeThrough) this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    else this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

/**
 *   Extension function used to change the text color
 *   of a AppCompatTextView
 *
 *   @param id a color id from the Android resource
 */
fun TextView.setTextColorFromResource(@ColorRes id: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, id))
}