package com.noom.interview.fullstack.sleep.infrastructure.util

import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import java.time.Duration
import java.time.Instant

const val DATE_ISO_8601_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$"
const val DATE_BASIC_YYYY_MM_DD = "^\\d{4}-\\d{2}-\\d{2}$"

fun getDifferenceOfTime(startTime: Instant, endTime: Instant): Double =
    Duration.between(startTime, endTime).toMillis() / 60000.0

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
