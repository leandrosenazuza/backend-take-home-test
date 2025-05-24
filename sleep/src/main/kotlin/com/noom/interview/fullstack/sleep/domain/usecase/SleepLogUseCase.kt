package com.noom.interview.fullstack.sleep.domain.usecase

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.springframework.data.domain.Page

interface SleepLogUseCase {
    fun createSleepLog(sleepLogRequest: SleepLogRequest): ApiResponse<SleepLogResponse?, Meta>
    fun updateSleepLog(sleepLogRequest: SleepLogRequest, idSleep: String): ApiResponse<SleepLogResponse?, Meta>
    fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta>
    fun getSleepLogByIdSleep(idSleep: String): ApiResponse<SleepLogResponse?, Meta>
    fun getLastNightSleepLogInformation(idUser: String): ApiResponse<SleepLogResponse?, Meta>
    fun getThirtyDaysLastAverageSleepLog(idUser: String, page: Int, pageSize: Int): ApiResponse<SleepLogResponse?, Meta>
}