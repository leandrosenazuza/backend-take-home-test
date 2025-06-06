package com.noom.interview.fullstack.sleep.domain.mapper.impl

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.mapper.SleepLogMapper
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.infrastructure.util.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class SleepLogMapperImplementation() : SleepLogMapper {
    override fun toSleepLogFromRequest(sleepLogRequest: SleepLogRequest) = SleepLog(
        idUser = sleepLogRequest.idUser,
        idSleep = UUID.randomUUID().toString(),
        dateSleep = getDateNowByServerMachine(),
        dateBedtimeStart = parseStringToInstant(sleepLogRequest.dateBedtimeStart),
        dateBedtimeEnd = parseStringToInstant(sleepLogRequest.dateBedtimeEnd),
        feelingMorning = sleepLogRequest.feelingMorning,
        dateCreate = getDateNowByServerMachine(),
    )

    override fun toUpdateSleepLogFromRequest(sleepLogRequest: SleepLogRequest, sleepLog: SleepLog) = SleepLog(
        idUser = sleepLogRequest.idUser,
        idSleep = sleepLog.idSleep,
        dateSleep = sleepLog.dateSleep,
        dateBedtimeStart = parseStringToInstant(sleepLogRequest.dateBedtimeStart),
        dateBedtimeEnd = parseStringToInstant(sleepLogRequest.dateBedtimeEnd),
        feelingMorning = sleepLogRequest.feelingMorning,
        dateCreate = getDateNowByServerMachine(),
    )

    override fun toResponseFromSleepLog(sleepLog: SleepLog) = SleepLogResponse(
        idUser = sleepLog.idUser,
        idSleep = sleepLog.idSleep,
        dateSleep = formatTodayDate(sleepLog.dateSleep),
        dateBedtimeStart = sleepLog.dateBedtimeStart.toString(),
        dateBedtimeEnd = sleepLog.dateBedtimeEnd.toString(),
        totalTimeInBedMinutes = sleepLog.totalTimeInBedMinutes,
        totalTimeInBedFormatted = formatTimeInBed(sleepLog.totalTimeInBedMinutes),
        dateBedtimeStartAndEndFormatted = formatStartAndEndInterval(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd),
        feelingMorning = MorningFeelingEnum.fromString(sleepLog.feelingMorning)?.displayName ?: ""
    )
}