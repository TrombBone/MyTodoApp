package com.example.mytodoapp.features.database.converters

import android.content.Context
import android.text.format.DateFormat
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DateTimeConverter private constructor() {

    companion object {
        private const val FORMAT_TIME_12_HOUR = "hh:mm aa" // 11:22 PM
        private const val FORMAT_TIME_24_HOUR = "HH:mm"    // 23:22

        private const val FORMAT_DATE = "LLL dd" // Jan 10
        private const val FORMAT_DATE_SHORT = "dd.MM" // 10.01
        private const val FORMAT_DATE_WITH_YEAR = "LLL dd YYYY" // Jan 10 2024
        private const val FORMAT_DATE_WITH_YEAR_SHORT = "dd.MM.YY" // 10.01.2024

        private const val FORMAT_DATE_TIME_12_HOUR = "LLL dd, hh:mm aa" // Jan 10, 11:22 PM
        private const val FORMAT_DATE_TIME_24_HOUR = "LLL dd, HH:mm"    // Jan 10, 23:22
        private const val FORMAT_DATE_TIME_12_HOUR_SHORT = "dd.MM, hh:mm aa" // 10.01, 11:22 PM
        private const val FORMAT_DATE_TIME_24_HOUR_SHORT = "dd.MM, HH:mm"    // 10.01, 23:22
        private const val FORMAT_DATE_TIME_WITH_YEAR_12_HOUR =
            "LLL dd YYYY, hh:mm aa" // Jan 10 2024, 11:22 PM
        private const val FORMAT_DATE_TIME_WITH_YEAR_24_HOUR =
            "LLL dd YYYY, HH:mm"    // Jan 10 2024, 23:22
        private const val FORMAT_DATE_TIME_WITH_YEAR_12_HOUR_SHORT =
            "dd.MM.YY, hh:mm aa" // 10.01.2024, 11:22 PM
        private const val FORMAT_DATE_TIME_WITH_YEAR_24_HOUR_SHORT =
            "dd.MM.YY, HH:mm"    // 10.01.2024, 23:22

        fun getTimeFormatter(context: Context): DateTimeFormatter {
            val pattern = if (DateFormat.is24HourFormat(context))
                FORMAT_TIME_24_HOUR
            else FORMAT_TIME_12_HOUR

            return DateTimeFormatter.ofPattern(pattern)
        }

        fun getDateFormatter(
            isShort: Boolean = false,
            withYear: Boolean = false
        ): DateTimeFormatter {
            val pattern = if (isShort)
                if (withYear) FORMAT_DATE_WITH_YEAR_SHORT
                else FORMAT_DATE_SHORT
            else
                if (withYear) FORMAT_DATE_WITH_YEAR
                else FORMAT_DATE

            return DateTimeFormatter.ofPattern(pattern)
        }

        fun getDateTimeFormatter(
            context: Context,
            isShort: Boolean = false,
            withYear: Boolean = false
        ): DateTimeFormatter {
            val is24Hour: Boolean = DateFormat.is24HourFormat(context)

            val pattern = if (isShort) {
                if (withYear) {
                    if (is24Hour)
                        FORMAT_DATE_TIME_WITH_YEAR_24_HOUR_SHORT
                    else FORMAT_DATE_TIME_WITH_YEAR_12_HOUR_SHORT
                } else {
                    if (is24Hour)
                        FORMAT_DATE_TIME_24_HOUR_SHORT
                    else FORMAT_DATE_TIME_12_HOUR_SHORT
                }
            } else {
                if (withYear) {
                    if (is24Hour)
                        FORMAT_DATE_TIME_WITH_YEAR_24_HOUR
                    else FORMAT_DATE_TIME_WITH_YEAR_12_HOUR
                } else {
                    if (is24Hour)
                        FORMAT_DATE_TIME_24_HOUR
                    else FORMAT_DATE_TIME_12_HOUR
                }
            }

            return DateTimeFormatter.ofPattern(pattern)
        }

        @JvmStatic
        @TypeConverter
        fun fromLocalDate(localDate: LocalDate?): String? {
            return if (localDate != null)
                DateTimeFormatter.ISO_LOCAL_DATE.format(localDate)
            else null
        }

        @JvmStatic
        @TypeConverter
        fun toLocalDate(date: String?): LocalDate? {
            return if (date.isNullOrEmpty()) null
            else LocalDate.parse(date)
        }

        @JvmStatic
        @TypeConverter
        fun toLocalDateTime(time: String?): LocalDateTime? {
            return if (time.isNullOrEmpty()) null
            else LocalDateTime.parse(time)
        }

        @JvmStatic
        @TypeConverter
        fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
            return if (localDateTime != null)
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime)
            else null
        }

        @JvmStatic
        @TypeConverter
        fun toLocalTime(time: String?): LocalTime? {
            return if (time.isNullOrEmpty()) null
            else LocalTime.parse(time)
        }

        @JvmStatic
        @TypeConverter
        fun fromLocalTime(time: LocalTime?): String? {
            return if (time != null) DateTimeFormatter.ISO_LOCAL_TIME.format(time) else null
        }
    }

}