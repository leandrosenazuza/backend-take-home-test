package com.noom.interview.fullstack.sleep.domain.json.request

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.infrastructure.util.DATE_ISO_8601_PATTERN
import com.noom.interview.fullstack.sleep.infrastructure.util.UUID_PATTERN
import javax.validation.constraints.Pattern

data class SleepLogRequest (
    @field:Pattern(regexp = UUID_PATTERN)
    val idUser: String = "",

    @field:Pattern(regexp = DATE_ISO_8601_PATTERN)
    val dateBedtimeStart: String = "",

    @field:Pattern(regexp = DATE_ISO_8601_PATTERN)
    val dateBedtimeEnd: String = "",

    @field:Pattern(regexp = "GOOD|BAD|OK")
    val feelingMorning: String = MorningFeelingEnum.entries.toString()
)

