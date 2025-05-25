package com.noom.interview.fullstack.sleep.infrastructure.util

import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import java.time.*
import java.time.temporal.ChronoUnit

const val DATE_ISO_8601_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$"
const val DATE_BASIC_YYYY_MM_DD = "^\\d{4}-\\d{2}-\\d{2}$"

fun getDifferenceOfTime(startTime: Instant, endTime: Instant): Double =
    Duration.between(startTime, endTime).toMillis() / 60000.0

fun localDateTimeToInstant(date: LocalDate, hour: Int, minute: Int): Instant =
    date.atTime(hour, minute).atZone(getZoneId()).toInstant()

fun parseStringToInstant(date: String): Instant {
    if (date.matches(Regex(DATE_BASIC_YYYY_MM_DD)))
        return Instant.parse(date.plus("T00:00:00Z"))
    else if (date.matches(Regex(DATE_ISO_8601_PATTERN))) {
        return Instant.parse(date)
    } else throw BadRequestException()
}

fun getTruncDate(date: Instant): String {
    val dateString = date.toString()
    return dateString.removeRange(10, dateString.length)
}

fun getZoneId() = ZoneId.systemDefault()

fun getDateThirtyDaysLastByServerMachine() = ZonedDateTime.now(getZoneId()).minus(30, ChronoUnit.DAYS).toInstant()

fun getDateNowByServerMachine() = ZonedDateTime.now(getZoneId()).toInstant()