package com.example.mytodoapp.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class MySharedPreferenceManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val sharedPreference by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    enum class Theme {
        SYSTEM, DARK, LIGHT;

        companion object {
            fun parse(s: String?): Theme {
                return when (s) {
                    DARK.toString() -> DARK
                    LIGHT.toString() -> LIGHT
                    else -> SYSTEM
                }
            }
        }
    }

    var theme: Theme
        get() = Theme.parse(
            sharedPreference.getString(
                PREFERENCE_THEME,
                Theme.SYSTEM.toString()
            )
        )
        set(value) {
            sharedPreference.edit().run {
                putString(PREFERENCE_THEME, value.toString())
                apply()
            }
        }

    var previousBackupDate: LocalDateTime?
        get() = DateTimeConverter.toLocalDateTime(
            sharedPreference.getString(PREFERENCE_BACKUP, null)
        )
        set(value) {
            sharedPreference.edit().run {
                putString(
                    PREFERENCE_BACKUP,
                    DateTimeConverter.fromLocalDateTime(value)
                )
                apply()
            }
        }

    var reminderTime: LocalTime?
        get() = DateTimeConverter.toLocalTime(
            sharedPreference.getString(PREFERENCE_REMINDER_TIME, "08:30")
        )
        set(value) {
            sharedPreference.edit().run {
                putString(PREFERENCE_REMINDER_TIME, DateTimeConverter.fromLocalTime(value))
                apply()
            }
        }

    val taskReminder: Boolean
        get() = sharedPreference.getBoolean(PREFERENCE_TASK_NOTIFICATION, true)

    val allowWeekNumbers: Boolean
        get() = sharedPreference.getBoolean(PREFERENCE_ALLOW_WEEK_NUMBERS, false)

    val reminderFrequency: String
        get() = sharedPreference.getString(
            PREFERENCE_REMINDER_FREQUENCY,
            DURATION_EVERYDAY
        ) ?: DURATION_EVERYDAY

    val taskReminderInterval: String
        get() = sharedPreference.getString(
            PREFERENCE_TASK_NOTIFICATION_INTERVAL,
            TASK_REMINDER_INTERVAL_3_HOURS
        ) ?: TASK_REMINDER_INTERVAL_3_HOURS

    companion object {
        const val DURATION_EVERYDAY = "EVERYDAY"
        const val DURATION_WEEKENDS = "WEEKENDS"

        const val TASK_REMINDER_INTERVAL_1_HOUR = "1"
        const val TASK_REMINDER_INTERVAL_3_HOURS = "3"
        const val TASK_REMINDER_INTERVAL_24_HOURS = "24"

        const val PREFERENCE_THEME = "KEY_THEME"
        const val PREFERENCE_REMINDER_FREQUENCY = "KEY_REMINDER_FREQUENCY"
        const val PREFERENCE_REMINDER_TIME = "KEY_REMINDER_TIME"
        const val PREFERENCE_TASK_NOTIFICATION = "KEY_TASK_NOTIFICATION"
        const val PREFERENCE_TASK_NOTIFICATION_INTERVAL = "KEY_TASK_NOTIFICATION_INTERVAL"
        const val PREFERENCE_ALLOW_WEEK_NUMBERS = "KEY_ALLOW_WEEK_NUMBERS"
        const val PREFERENCE_BACKUP = "KEY_BACKUP"
    }
}