package com.noom.interview.fullstack.sleep.domain.usecase

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta

interface SleepLogUseCase {
    fun getSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta>
    fun createSleepLog(userRequest: SleepLogRequest): ApiResponse<SleepLogResponse?, Meta>
    fun updateSleepLog(userRequest: SleepLogRequest, userId: String): ApiResponse<SleepLogResponse?, Meta>
    fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta>
}