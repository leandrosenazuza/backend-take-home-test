package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.mapper.SleepLogMapper
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.model.specification.SleepLogSpecification
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
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
@Service
class SleepLogUseCaseImplementation(
    @Autowired val sleepLogRepository: SleepLogRepository,
    @Autowired val sleepLogMapper: SleepLogMapper,
    @Autowired val userUseCase: UserUseCase
) : SleepLogUseCase {

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
        val sleepLog = this.getSleepLog(idSleep)
        if (sleepLog != null) {
            val sleepLogUpdated: SleepLog = sleepLogMapper.toSleepLogFromRequest(sleepLogRequest)
            sleepLogUpdated.totalTimeInBedMinutes =  getDifferenceOfTime(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
            sleepLogRepository.save(sleepLogUpdated)
            val data: SleepLogResponse = sleepLogMapper.toResponseFromSleepLog(sleepLog)
            return ApiResponse.Builder<SleepLogResponse, Meta>().status("success").data(data)
                .message("Sleep updated with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        val sleepLog = this.getSleepLog(idSleep)
        if (sleepLog != null) {
            sleepLogRepository.delete(sleepLog)
            val data: SleepLogResponse = sleepLogMapper.toResponseFromSleepLog(sleepLog)
            return ApiResponse.Builder<SleepLogResponse, Meta>().status("success").data(data)
                .message("SleepLog deleted with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun getSleepLogByIdSleep(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        val sleepLog = this.getSleepLog(idSleep)
        if (sleepLog != null) {
            return ApiResponse.Builder<SleepLogResponse, Meta>()
                .status("success")
                .data(sleepLogMapper.toResponseFromSleepLog(sleepLog))
                .message("SleepLog returned with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun getLastNightSleepLogInformation(idUser: String): ApiResponse<SleepLogResponse?, Meta> {
        val sleepLog = getSleepLogByIdUser(idUser)
        if (sleepLog != null) {
            return ApiResponse.Builder<SleepLogResponse, Meta>()
                .status("success")
                .data(sleepLogMapper.toResponseFromSleepLog(sleepLog))
                .message("SleepLog returned with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun getThirtyDaysLastAverageSleepLog(idUser: String, page: Int, pageSize: Int): ApiResponse<SleepLogResponse?, Meta> {
        val sleepLogPage = getLastThirtyDaysSleepLogByIdUser(idUser, page, pageSize)
        val sleepList = sleepLogPage.content.map { sleepLog -> sleepLogMapper.toResponseFromSleepLog(sleepLog) }

        if (sleepList.isNotEmpty()) {
            return ApiResponse.Builder<SleepLogResponse, Meta>()
                .status("success")
                .dataList(sleepList)
                .message("SleepLog returned with success!")
                .meta(Meta(sleepLogPage.size, sleepLogPage.totalPages, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    private fun getSleepLog(idSleep: String) = sleepLogRepository.findByIdSleepLog(idSleep)

    private fun getSleepLogByIdUser(idUser: String) = sleepLogRepository.findAll(SleepLogSpecification(idUser)).get(0)

    private fun getLastThirtyDaysSleepLogByIdUser(idUser: String, page: Int, pageSize: Int): Page<SleepLog> {
        val pageable = PageRequest.of(page - 1, pageSize)
        return sleepLogRepository.findAll(SleepLogSpecification(idUser, true), pageable)
    }

    private fun validateRequest(sleepLogRequest: SleepLogRequest) {
        validateDates(sleepLogRequest)
        validateUser(sleepLogRequest.idUser)
        validateSleepLogAlreadyExist(sleepLogRequest.idUser)
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

    private fun validateSleepLogAlreadyExist(idUser: String) {
        if(sleepLogRepository.findAll(SleepLogSpecification(idUser)).size != 0){
            throw BadRequestException()
        }
    }

    private fun validateUser(idUser: String) {
        if(userUseCase.getUserById(idUser) == null) {
            throw BadRequestException()
        }
    }

    private fun validateDates(sleepLogRequest: SleepLogRequest) {
        val instantNow = Instant.now().atZone(ZoneId.systemDefault())
        val dateSleep = instantNow.toString()
        val dateSleepMinusOne = instantNow.minusDays(1).toString()
        val startDateHourofSleep = sleepLogRequest.dateBedtimeStart
        val endDateHourofSleep = sleepLogRequest.dateBedtimeEnd

        if(!(dateSleep.removeRange(10, dateSleep.length) == startDateHourofSleep.removeRange(10, startDateHourofSleep.length)
            || dateSleepMinusOne.removeRange(10, dateSleep.length) == startDateHourofSleep.removeRange(10, startDateHourofSleep.length))){
            throw BadRequestException()
        }

        if (!startDateHourofSleep.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }

        if (!endDateHourofSleep.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }

        if (getDifferenceOfTime(Instant.parse(startDateHourofSleep), Instant.parse(endDateHourofSleep)) < 0) throw BadRequestException()
    }
}

