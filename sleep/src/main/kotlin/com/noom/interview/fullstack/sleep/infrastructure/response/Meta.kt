package com.noom.interview.fullstack.sleep.infrastructure.response

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class Meta(
    val totalRecords: Int = 1,
    val totalPages: Int = 1,
    @get:Pattern(
        regexp = "(^(\\d{4})-(1[0-2]|0?[1-9])-(3[01]|[12][0-9]|0?[1-9])T(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)Z$)"
    )
    @get:Size(max = 20)
    val requestDateTime: String = ZonedDateTime.now(ZoneId.systemDefault()).toInstant().truncatedTo(ChronoUnit.SECONDS).toString()
)