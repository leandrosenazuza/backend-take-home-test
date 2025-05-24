package com.noom.interview.fullstack.sleep.domain.mapper

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.model.SleepLog

interface SleepLogMapper {
    fun toSleepLogFromRequest(sleepLogRequest: SleepLogRequest): SleepLog
    fun toUpdateSleepLogFromRequest(sleepLogRequest: SleepLogRequest, sleepLog: SleepLog): SleepLog
    fun toResponseFromSleepLog(sleepLog: SleepLog): SleepLogResponse
}