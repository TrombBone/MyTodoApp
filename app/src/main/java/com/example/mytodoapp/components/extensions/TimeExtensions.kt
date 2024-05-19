package com.example.mytodoapp.components.extensions

import java.time.LocalTime

fun LocalTime.isCurrentHour() = this.hour == LocalTime.now().hour

fun LocalTime.isCurrentMinute() = this.minute == LocalTime.now().minute

fun LocalTime.isCurrentHourAndMinute() = isCurrentHour() && isCurrentMinute()

/**
 * Checks that the entered time is future compared to the current one
 * with an accuracy of up to 1 minute inclusive.
 *
 * examples:
 * - At the current time of 12:34:56 and the entered time of 12:34:00 or more returned true.
 * - At the current time of 12:34:56 and the entered time of 12:33:00 or less returned false.
 *
 * @return if entered time is after
 */
fun LocalTime.isAfterNowMinuteInclude() =
    this.isAfter(LocalTime.of(LocalTime.now().hour, LocalTime.now().minute - 1))

/**
 * Checks that the entered time is past compared to the current one
 * with an accuracy of up to 1 minute inclusive.
 *
 * examples:
 * - At the current time of 12:34:56 and the entered time of 12:34:00 and less returned true.
 * - At the current time of 12:34:56 and the entered time of 12:35:00 or more returned false.
 *
 * @return if entered time is before
 */
fun LocalTime.isBeforeNowMinute() =
    this.isBefore(LocalTime.of(LocalTime.now().hour, LocalTime.now().minute + 1))