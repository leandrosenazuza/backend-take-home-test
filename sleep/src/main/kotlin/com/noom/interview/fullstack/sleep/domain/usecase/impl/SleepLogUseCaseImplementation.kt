package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.mapper.SleepLogMapper
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.usecase.SleepLogUseCase
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import com.noom.interview.fullstack.sleep.infrastructure.exception.NotFoundException
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import com.noom.interview.fullstack.sleep.infrastructure.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SleepLogUseCaseImplementation(
    @Autowired val sleepLogRepository: SleepLogRepository,
    @Autowired val sleepLogMapper: SleepLogMapper,
    @Autowired val userUseCase: UserUseCase
) : SleepLogUseCase {
    override fun getSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }

    override fun createSleepLog(sleepLogRequest: SleepLogRequest): ApiResponse<SleepLogResponse?, Meta> {
        validateRequest(sleepLogRequest)
        val sleepLog: SleepLog = sleepLogMapper.toSleepLogFromRequest(sleepLogRequest)
        sleepLog.totalTimeInBedMinutes = getDifferenceOfTime(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
        sleepLogRepository.save(sleepLog)
        val data: SleepLogResponse = sleepLogMapper.toResponseFromSleepLog(sleepLog)
        return ApiResponse.Builder<SleepLogResponse, Meta>().status("success").data(data)
            .message("SleepLog created with success!")
            .meta(Meta(1, 1, Instant.now().toString())).build()
    }
    
    override fun updateSleepLog(sleepLogRequest: SleepLogRequest, idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        validateDates(sleepLogRequest)
        val sleepLog = sleepLogRepository.findByIdSleepLog(idSleep)
        if (sleepLog != null) {
            val sleepLogUpdated: SleepLog = sleepLogMapper.toSleepLogFromRequest(sleepLogRequest)
            sleepLogUpdated.totalTimeInBedMinutes =  getDifferenceOfTime(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
            sleepLogRepository.save(sleepLogUpdated)
            val data: SleepLogResponse = sleepLogMapper.toResponseFromSleepLog(sleepLog)
            return ApiResponse.Builder<SleepLogResponse, Meta>().status("success").data(data)
                .message("User updated with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }

    override fun getSleepLogByIdSleep(idSleep: String): ApiResponse<SleepLogResponse, Meta> {
        TODO("Not yet implemented")
    }

    override fun getLastNightSleepLogInformation(idUser: String): ApiResponse<SleepLogResponse, Meta> {
        TODO("Not yet implemented")
    }

    override fun getThirtyDaysLastAverageSleepLog(idUser: String): ApiResponse<SleepLogResponse, Meta> {
        TODO("Not yet implemented")
    }

    override fun getSleepLogList(idUser: String): ApiResponse<Page<SleepLogResponse>, Meta> {
        TODO("Not yet implemented")
    }

    private fun validateRequest(sleepLogRequest: SleepLogRequest) {
        validateDates(sleepLogRequest)
        validateUser(sleepLogRequest.idUser)
        validateFeelingEnum(sleepLogRequest.feelingMorning)
    }

    private fun validateFeelingEnum(feelingMorning: String) {
        var isNotEqual = false
        for(feel in MorningFeelingEnum.entries) {
            if(feelingMorning.equals(feel.toString())){
                isNotEqual = true
            }
        }
        if(!isNotEqual){
            throw BadRequestException()
        }
    }


    private fun validateUser(idUser: String) {
        if(userUseCase.getUserById(idUser) == null) {
            throw BadRequestException()
        }
    }

    private fun validateDates(sleepLogRequest: SleepLogRequest) {
        if (!sleepLogRequest.dateSleep.matches(Regex(DATE_BASIC_YYYY_MM_DD))) {
            throw BadRequestException()
        }

        if (!sleepLogRequest.dateBedtimeStart.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }

        if (!sleepLogRequest.dateBedtimeEnd.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }
    }
}