package com.noom.interview.fullstack.sleep.infrastructure.controller

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogAvgLastThirtyDaysResponse
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.springframework.http.ResponseEntity

interface SleepLogController {
    fun createSleepLog(sleepLogRequest: SleepLogRequest): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>>
    fun updateSleepLog(sleepLogRequest: SleepLogRequest, idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>>
    fun deleteSleepLog(idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>>
    fun getSleepLogByIdSleep(idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>>
    fun getLastNightSleepLogInformation(idUser: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>>
    fun getThirtyDaysLastAverageSleepLog(idUser: String): ResponseEntity<ApiResponse<SleepLogAvgLastThirtyDaysResponse?, Meta>>
    fun getAllSleepLogByidUser(idUser: String, page: Int, pageSize: Int): ResponseEntity<ApiResponse<List<SleepLogResponse>?, Meta>>
}