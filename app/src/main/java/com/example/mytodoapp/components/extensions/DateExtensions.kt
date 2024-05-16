package com.example.mytodoapp.components.extensions

import java.time.LocalDate

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is same as the date today
 *
 *   @return true if the date matches from the current date
 */
fun LocalDate.isToday(): Boolean = LocalDate.now() == this

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is the same as the next day
 *
 *   @return true if the ZonedDateTime object is the
 *           same as the next day
 */
fun LocalDate.isTomorrow(): Boolean = LocalDate.now().plusDays(1) == this

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is the same as the previous day
 *
 *   @return true if the ZonedDateTime object is the
 *           same as the previous day
 */
fun LocalDate.isYesterday(): Boolean = LocalDate.now().minusDays(1) == this

/**
 *   An extension function used to determine
 *   if the ZonedDateTime object is before
 *   the current datetime
 *
 *   @return true if the current ZonedDateTime object
 *              is before the current date-time
 */
fun LocalDate.isBeforeToday(): Boolean = this.isBefore(LocalDate.now())

/**
 *   An extension function used to determine
 *   if the ZonedDateTime object is after
 *   the current datetime
 *
 *   @return true if the current ZonedDateTime object
 *           is after the current date-time
 */
fun LocalDate.isAfterToday(): Boolean = this.isAfter(LocalDate.now())