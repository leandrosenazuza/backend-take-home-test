package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.noom.interview.fullstack.sleep.domain.constants.*
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogAvgLastThirtyDaysResponse
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.usecase.SleepLogUseCase
import com.noom.interview.fullstack.sleep.infrastructure.controller.SleepLogController
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SleepLogControllerImplementation(
    @Autowired val sleepLogUseCase: SleepLogUseCase
) : SleepLogController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /*
    * The purpose of this endpoint is to register sleep log when the user forgot to add some day.
    * In this case, the user must insert as dateSleep the respective "today" date for the interval inserted.
    * It's not allowed to insert one date that already exist.
    * */
    @PostMapping(URI_POST_SLEEP_LOG_V1)
    override fun createSleepLog(@RequestBody sleepLogRequest: SleepLogRequest): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to POST SleepLog by body: $sleepLogRequest")
        val sleepLog = sleepLogUseCase.createSleepLog(sleepLogRequest)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to modify some sleep log when the user notice that the log added in some day is wrong.
    * In this case, the user must insert as dateSleep the respective "today" date for the interval inserted.
    * It's not allowed to modify one date not exist.
    * */
    @PutMapping(URI_PUT_SLEEP_LOG_V1)
    override fun updateSleepLog(
        @RequestBody sleepLogRequest: SleepLogRequest, @PathVariable idSleep: String
    ): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to PUT SleepLog by ID: $idSleep")
        val sleepLog = sleepLogUseCase.updateSleepLog(sleepLogRequest, idSleep)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to delete some register.
    * The register have to exist.
    * */
    @DeleteMapping(URI_DELETE_SLEEP_LOG_V1)
    override fun deleteSleepLog(@PathVariable idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to DELETE SleepLog by body: $idSleep")
        val sleepLog = sleepLogUseCase.deleteSleepLog(idSleep)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to get some sleep log.
    * The register have to exist.
    * */
    @GetMapping(URI_GET_SLEEP_BY_ID_SLEEP_LOG_V1)
    override fun getSleepLogByIdSleep(@PathVariable idSleep: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to GET SleepLog by idSleep: $idSleep")
        val sleepLog = sleepLogUseCase.getSleepLogByIdSleep(idSleep)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to get the last night sleep log.
    * The register have to exist.
    * */
    @GetMapping(URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1)
    override fun getLastNightSleepLogInformation(@PathVariable idUser: String): ResponseEntity<ApiResponse<SleepLogResponse?, Meta>> {
        logger.info("Request to GET last night SleepLog by idUser: $idUser")
        val sleepLog = sleepLogUseCase.getLastNightSleepLogInformation(idUser)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to get the average time that the user used to went to sleep and to wake up.
    * When the date of some day in the 30 days does not exist, this date will be ignored in the average calculation, and will not affect the operation.
    * Only one register per day.
    * */
    @GetMapping(URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1)
    override fun getThirtyDaysLastAverageSleepLog(@PathVariable idUser: String): ResponseEntity<ApiResponse<SleepLogAvgLastThirtyDaysResponse?, Meta>> {
        logger.info("Request to GET AVERAGE of the last 30 days SleepLog by idUser: $idUser")
        val sleepLog = sleepLogUseCase.getThirtyDaysLastAverageSleepLog(idUser)
        return ResponseEntity.ok(sleepLog)
    }

    /*
    * The purpose of this endpoint is to list all sleep logs in a paginated response.
    * */
    @GetMapping(URI_GET_ALL_DAYS_SLEEP_BY_ID_USER_V1)
    override fun getAllSleepLogByidUser(
        @PathVariable idUser: String,
        @RequestParam(value = "page", defaultValue = PAGE_STANDARD.toString()) page: Int,
        @RequestParam(value = "page-size", defaultValue = PAGE_SIZE_STANDARD.toString()) pageSize: Int
    ): ResponseEntity<ApiResponse<List<SleepLogResponse>?, Meta>> {
        logger.info("Request to GET ALL SleepLog by idUser: $idUser")
        val sleepLog = sleepLogUseCase.getSleepLogPaginated(idUser, page, pageSize)
        return ResponseEntity.ok(sleepLog)
    }
}