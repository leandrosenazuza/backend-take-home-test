package com.noom.interview.fullstack.sleep.helper.model

import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun createSleepLogEntityMock(
    idSleep: String = UUID.randomUUID().toString(),
    idUser: String = UUID.randomUUID().toString(),
    dateSleep: Instant = Instant.now(),
    dateBedtimeStart: Instant = Instant.now().minus(8, ChronoUnit.HOURS),
    dateBedtimeEnd: Instant = Instant.now(),
    totalTimeInBedMinutes: Double = 0.0,
    feelingMorning: String = "GOOD",
    dateCreate: Instant = Instant.now()
) = SleepLog(
    idSleep = idSleep,
    idUser = idUser,
    dateSleep = dateSleep,
    dateBedtimeStart = dateBedtimeStart,
    dateBedtimeEnd = dateBedtimeEnd,
    totalTimeInBedMinutes = totalTimeInBedMinutes,
    feelingMorning = feelingMorning,
    dateCreate = dateCreate
)