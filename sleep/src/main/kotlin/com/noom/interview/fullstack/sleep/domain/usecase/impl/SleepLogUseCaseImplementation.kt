package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogAvgLastThirtyDaysResponse
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
import com.noom.interview.fullstack.sleep.infrastructure.util.DATE_ISO_8601_PATTERN
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateThirtyDaysLastByServerMachine
import com.noom.interview.fullstack.sleep.infrastructure.util.getDifferenceOfTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

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

    override fun updateSleepLog(
        sleepLogRequest: SleepLogRequest,
        idSleep: String
    ): ApiResponse<SleepLogResponse?, Meta> {
        validateDates(sleepLogRequest)
        val sleepLog = this.getSleepLog(idSleep)
        if (sleepLog != null) {
            val sleepLogUpdated: SleepLog = sleepLogMapper.toSleepLogFromRequest(sleepLogRequest)
            sleepLogUpdated.totalTimeInBedMinutes =
                getDifferenceOfTime(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
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

    override fun getThirtyDaysLastAverageSleepLog(idUser: String): ApiResponse<SleepLogAvgLastThirtyDaysResponse?, Meta> {
        val sleepLogList = getLastThirtyDaysSleepLogByIdUser(idUser)
        if (sleepLogList.isNotEmpty()) {
            return ApiResponse.Builder<SleepLogAvgLastThirtyDaysResponse, Meta>()
                .status("success")
                .data(
                    SleepLogAvgLastThirtyDaysResponse(
                        idUser = idUser,
                        fromDate = getDateThirtyDaysLastByServerMachine().toString(),
                        toDate = getDateNowByServerMachine().toString(),
                        averageTotalTimeInBed = getAvgTimeInBed(sleepLogList),
                        averageDateBedtimeStart = getAvgTimeHourMinuteSecond(sleepLogList),
                        averageDateBedtimeEnd = getAvgTimeHourMinuteSecond(sleepLogList),
                        qtdDaysGood = getQuantityOfMood(MorningFeelingEnum.GOOD.toString(), sleepLogList),
                        qtdDaysBad = getQuantityOfMood(MorningFeelingEnum.BAD.toString(), sleepLogList),
                        qtdDaysOk = getQuantityOfMood(MorningFeelingEnum.OK.toString(), sleepLogList),
                    )
                )
                .message("The sleep log average of the last 30 days return with success!")
                .build()
        } else throw NotFoundException()
    }

    private fun getAvgTimeHourMinuteSecond(sleepLogList: List<SleepLog>): String {
        var hour = 0
        var minute = 0
        var second = 0
        val size = sleepLogList.size

        for (sleepLog in sleepLogList) {
            val timeTrucate = sleepLog.dateBedtimeStart.toString()
            hour = timeTrucate.removeRange(10, 12).toInt()
            minute = timeTrucate.removeRange(13, 15).toInt()
            second = timeTrucate.removeRange(16, 18).toInt()
        }

        val avgHour = hour / size
        val avgMinute = minute / size
        val avgSecond = second / size

        return LocalDate.parse("$avgHour:$avgMinute:$avgSecond", DateTimeFormatter.ofPattern("HH:mm:ss")).toString()

    }

    private fun getQuantityOfMood(mood: String, sleepLogList: List<SleepLog>): Int {
        var quantity = 0
        for (item in sleepLogList) {
            if (item.feelingMorning == mood) ++quantity
        }
        return quantity
    }

    private fun getAvgTimeInBed(sleepLogList: List<SleepLog>): Double {
        var timeInBed = 0.0
        for (item in sleepLogList) {
            timeInBed = ++item.totalTimeInBedMinutes
        }
        return timeInBed / sleepLogList.size
    }

    override fun getSleepLogPaginated(
        idUser: String,
        page: Int,
        pageSize: Int
    ): ApiResponse<List<SleepLogResponse>?, Meta> {
        val sleepLogPage = getAllSleepLogPaginated(idUser, page, pageSize)
        val data = sleepLogPage.content.parallelStream().map { sleepLogMapper.toResponseFromSleepLog(it) }.toList()

        if (data != null) {
            return ApiResponse.Builder<List<SleepLogResponse>, Meta>()
                .status("success")
                .data(data)
                .message("SleepLog returned with success!")
                .meta(Meta(data.size, sleepLogPage.totalPages, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    private fun getSleepLog(idSleep: String) = sleepLogRepository.findByIdSleepLog(idSleep)

    private fun getSleepLogByIdUser(idUser: String) = sleepLogRepository.findAll(SleepLogSpecification(idUser)).get(0)

    private fun getAllSleepLogPaginated(idUser: String, page: Int, pageSize: Int): Page<SleepLog> {
        val pageable = PageRequest.of(page - 1, pageSize)
        return sleepLogRepository.findAll(SleepLogSpecification(idUser, true), pageable)
    }

    private fun getLastThirtyDaysSleepLogByIdUser(idUser: String): List<SleepLog> {
        return sleepLogRepository.findAll(SleepLogSpecification(idUser, true))
    }

    private fun validateRequest(sleepLogRequest: SleepLogRequest) {
        validateDates(sleepLogRequest)
        validateUser(sleepLogRequest.idUser)
        validateSleepLogAlreadyExist(sleepLogRequest.idUser)
        validateFeelingEnum(sleepLogRequest.feelingMorning)
    }

    private fun validateFeelingEnum(feelingMorning: String) {
        var isNotEqual = false
        for (feel in MorningFeelingEnum.entries) {
            if (feelingMorning.equals(feel.toString())) {
                isNotEqual = true
            }
        }
        if (!isNotEqual) {
            throw BadRequestException()
        }
    }

    private fun validateSleepLogAlreadyExist(idUser: String) {
        if (sleepLogRepository.findAll(SleepLogSpecification(idUser)).size != 0) {
            throw BadRequestException()
        }
    }

    private fun validateUser(idUser: String) {
        if (userUseCase.getUserById(idUser) == null) {
            throw BadRequestException()
        }
    }

    private fun validateDates(sleepLogRequest: SleepLogRequest) {
        val instantNow = Instant.now().atZone(ZoneId.systemDefault())
        val dateSleep = instantNow.toString()
        val dateSleepMinusOne = instantNow.minusDays(1).toString()
        val startDateHourofSleep = sleepLogRequest.dateBedtimeStart
        val endDateHourofSleep = sleepLogRequest.dateBedtimeEnd

        if (!(dateSleep.removeRange(10, dateSleep.length) == startDateHourofSleep.removeRange(
                10,
                startDateHourofSleep.length
            )
                    || dateSleepMinusOne.removeRange(10, dateSleep.length) == startDateHourofSleep.removeRange(
                10,
                startDateHourofSleep.length
            ))
        ) {
            throw BadRequestException()
        }

        if (!startDateHourofSleep.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }

        if (!endDateHourofSleep.matches(Regex(DATE_ISO_8601_PATTERN))) {
            throw BadRequestException()
        }

        if (getDifferenceOfTime(
                Instant.parse(startDateHourofSleep),
                Instant.parse(endDateHourofSleep)
            ) < 0
        ) throw BadRequestException()
    }
}

