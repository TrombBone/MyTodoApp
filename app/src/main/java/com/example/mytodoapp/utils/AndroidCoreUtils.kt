package com.example.mytodoapp.utils

import android.app.PendingIntent
import android.os.Build

fun flagUpdateCurrent(mutable: Boolean): Int {
    return if (mutable) {
        if (Build.VERSION.SDK_INT >= 31) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }
}