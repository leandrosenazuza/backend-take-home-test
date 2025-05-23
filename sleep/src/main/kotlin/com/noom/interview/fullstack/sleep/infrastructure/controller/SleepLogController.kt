package com.noom.interview.fullstack.sleep.infrastructure.controller

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta

interface SleepLogController {
    fun createSleepLog(sleepLogRequest: SleepLogRequest): ApiResponse<SleepLogResponse, Meta>
    fun updateSleepLog(sleepLogRequest: SleepLogRequest): ApiResponse<SleepLogResponse, Meta>
    fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse, Meta>
    fun getSleepLog(idSleep: String): ApiResponse<SleepLogResponse, Meta>
}