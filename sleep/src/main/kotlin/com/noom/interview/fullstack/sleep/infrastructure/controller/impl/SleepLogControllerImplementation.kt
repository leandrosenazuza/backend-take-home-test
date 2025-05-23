package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.noom.interview.fullstack.sleep.domain.constants.URI_POST_SLEEP_LOG_V1
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.usecase.SleepLogUseCase
import com.noom.interview.fullstack.sleep.infrastructure.controller.SleepLogController
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SleepLogControllerImplementation(
    @Autowired val sleepLogUseCase: SleepLogUseCase
) : SleepLogController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping(URI_POST_SLEEP_LOG_V1)
    override fun createSleepLog(@RequestBody sleepLogRequest: SleepLogRequest): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to POST SleepLog by body: + : + $sleepLogRequest")
        val sleepLog = sleepLogUseCase.createSleepLog(sleepLogRequest)
        return ResponseEntity.ok(sleepLog)
    }

    override fun updateSleepLog(sleepLogRequest: SleepLogRequest): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        TODO("Not yet implemented")
    }

    override fun deleteSleepLog(idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        TODO("Not yet implemented")
    }

    override fun getSleepLogByIdSleep(idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        TODO("Not yet implemented")
    }

    override fun getLastNightSleepLogInformation(idUser: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        TODO("Not yet implemented")
    }

    override fun getThirtyDaysLastAverageSleepLog(idUser: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        TODO("Not yet implemented")
    }

    override fun getSleepLogList(idUser: String): ResponseEntity<ApiResponse<Page<SleepLogResponse>, Meta>> {
        TODO("Not yet implemented")
    }

}