package com.example.mytodoapp.components.extensions

import java.time.LocalDate

fun LocalDate.isToday(): Boolean = LocalDate.now() == this

fun LocalDate.isTomorrow(): Boolean = LocalDate.now().plusDays(1) == this

fun LocalDate.isYesterday(): Boolean = LocalDate.now().minusDays(1) == this

fun LocalDate.isBeforeToday(): Boolean = this.isBefore(LocalDate.now())

fun LocalDate.isAfterToday(): Boolean = this.isAfter(LocalDate.now())