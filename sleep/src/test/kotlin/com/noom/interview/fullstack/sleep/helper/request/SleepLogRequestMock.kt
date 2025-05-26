package com.noom.interview.fullstack.sleep.helper.request

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.infrastructure.util.getZoneId
import java.time.LocalDate

fun createSleepLogRequestMock(
    idUser: String = "123e4567-e89b-12d3-a456-426614174000",
    dateSleep: LocalDate = LocalDate.now(getZoneId()),
    bedtimeStart: String? = null,
    bedtimeEnd: String? = null,
    feelingMorning: String = MorningFeelingEnum.GOOD.toString()
): SleepLogRequest {
    return SleepLogRequest(
        idUser = idUser,
        dateSleep = dateSleep.toString(),
        dateBedtimeStart = bedtimeStart.toString(),
        dateBedtimeEnd = bedtimeEnd.toString(),
        feelingMorning = feelingMorning
    )
}