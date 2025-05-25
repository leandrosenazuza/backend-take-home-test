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
import com.noom.interview.fullstack.sleep.infrastructure.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.*
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
        if (sleepLog.size > 0) {
            return ApiResponse.Builder<SleepLogResponse, Meta>()
                .status("success")
                .data(sleepLogMapper.toResponseFromSleepLog(sleepLog.get(0)))
                .message("SleepLog returned with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun getThirtyDaysLastAverageSleepLog(idUser: String): ApiResponse<SleepLogAvgLastThirtyDaysResponse?, Meta> {
        val sleepLogList = getLastThirtyDaysSleepLogByIdUser(idUser)
        if (sleepLogList.isNotEmpty()) {
            val data = SleepLogAvgLastThirtyDaysResponse(
                idUser = idUser,
                fromDate = getDateThirtyDaysLastByServerMachine().toString(),
                toDate = getDateNowByServerMachine().toString(),
                qtdDaysGood = getQuantityOfMood(MorningFeelingEnum.GOOD.toString(), sleepLogList),
                qtdDaysBad = getQuantityOfMood(MorningFeelingEnum.BAD.toString(), sleepLogList),
                qtdDaysOk = getQuantityOfMood(MorningFeelingEnum.OK.toString(), sleepLogList),
            )

            data.averageDateBedtimeStartAndEndFormatted = formatStartAndEndInterval(
                getAvgTimeHourMinuteSecondStart(sleepLogList),
                getAvgTimeHourMinuteSecondEnd(sleepLogList)
            )
            data.averageTotalTimeInBedFormatted = formatTimeInBed(
                calculateTotalTimeInBedAsDouble(
                    data.averageDateBedtimeStartAndEndFormatted)
            )

            return ApiResponse.Builder<SleepLogAvgLastThirtyDaysResponse, Meta>()
                .status("success")
                .data(data)
                .message("The sleep log average of the last 30 days return with success!")
                .build()
        } else throw NotFoundException()
    }

    private fun calculateTotalTimeInBedAsDouble(interval: String): Double {
        val times = interval.split(" - ")
        val formatter = DateTimeFormatter.ofPattern("h:mm a")

        val start = LocalTime.parse(times[0].uppercase(), formatter)
        val end = LocalTime.parse(times[1].uppercase(), formatter)

        var durationMinutes = Duration.between(start, end).toMinutes()
        if (durationMinutes < 0) {
            durationMinutes += 24 * 60
        }

        return durationMinutes.toDouble()
    }

    private fun getAvgTimeHourMinuteSecondStart(sleepLogList: List<SleepLog>): Instant {
        var totalSeconds = 0L

        for (sleepLog in sleepLogList) {
            val localTime = sleepLog.dateBedtimeStart.atZone(getZoneId()).toLocalTime()
            totalSeconds += localTime.toSecondOfDay()
        }

        val avgSeconds = totalSeconds / sleepLogList.size
        val avgTime = LocalTime.ofSecondOfDay(avgSeconds)

        val today = ZonedDateTime.now(getZoneId()).toLocalDate()
        val zonedDateTime = avgTime.atDate(today).atZone(getZoneId())

        return zonedDateTime.toInstant()
    }

    private fun getAvgTimeHourMinuteSecondEnd(sleepLogList: List<SleepLog>): Instant {
        var totalSeconds = 0L

        for (sleepLog in sleepLogList) {
            var localTime = sleepLog.dateBedtimeEnd.atZone(getZoneId()).toLocalTime()

            // Adjust for crossing midnight
            if (localTime.isBefore(sleepLog.dateBedtimeStart.atZone(getZoneId()).toLocalTime())) {
                localTime = localTime.plusHours(24)
            }

            totalSeconds += localTime.toSecondOfDay()
        }

        val avgSeconds = totalSeconds / sleepLogList.size
        val avgTime = LocalTime.ofSecondOfDay(avgSeconds)

        val today = ZonedDateTime.now(getZoneId()).toLocalDate()
        val zonedDateTime = avgTime.atDate(today).atZone(getZoneId())

        return zonedDateTime.toInstant()
    }

    private fun getAvgTimeHourMinuteSecondEndString(sleepLogList: List<SleepLog>): String {
        var totalSeconds = 0L

        for (sleepLog in sleepLogList) {
            val localTime = sleepLog.dateBedtimeEnd.atZone(getZoneId()).toLocalTime()
            totalSeconds += localTime.toSecondOfDay()
        }

        val avgSeconds = totalSeconds / sleepLogList.size
        val avgTime = LocalTime.ofSecondOfDay(avgSeconds)

        return avgTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
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
        if (sleepLogPage.isEmpty) throw NotFoundException()
        val data = sleepLogPage.content.parallelStream().map { sleepLogMapper.toResponseFromSleepLog(it) }.toList()
        return ApiResponse.Builder<List<SleepLogResponse>, Meta>()
            .status("success")
            .data(data)
            .message("SleepLog returned with success!")
            .meta(
                Meta(
                    totalRecords = sleepLogPage.totalElements.toInt(),
                    totalPages = sleepLogPage.totalPages,
                    requestDateTime = Instant.now().toString()
                )
            ).build()
    }

    private fun getSleepLog(idSleep: String) = sleepLogRepository.findByIdSleepLog(idSleep)

    private fun getSleepLogByIdUser(idUser: String) = sleepLogRepository.findAll(SleepLogSpecification(idUser))

    private fun getAllSleepLogPaginated(idUser: String, page: Int, pageSize: Int): Page<SleepLog> {
        val pageable = PageRequest.of(page - 1, pageSize)
        return sleepLogRepository.findAll(SleepLogSpecification(idUser), pageable)
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

    fun validateDates(sleepLogRequest: SleepLogRequest) {

        fun parseInstant(str: String): Instant? =
            runCatching { Instant.parse(str) }.getOrNull()

        val startInstant = parseInstant(sleepLogRequest.dateBedtimeStart)
            ?: throw BadRequestException("Start date not in valid ISO8601 format.")
        val endInstant = parseInstant(sleepLogRequest.dateBedtimeEnd)
            ?: throw BadRequestException("End date not in valid ISO8601 format.")

        if (!endInstant.isAfter(startInstant)) {
            throw BadRequestException("End time must be after start time.")
        }

        val systemZone = ZoneId.systemDefault()
        val nowDate = LocalDate.now(systemZone)
        val yesterdayDate = nowDate.minusDays(1)

        val startDate = startInstant.atZone(systemZone).toLocalDate()
        val endDate = endInstant.atZone(systemZone).toLocalDate()

        if (startDate == nowDate || startDate == yesterdayDate) {
            if (endDate != nowDate && endDate != startDate) {
                throw BadRequestException(
                    "For recent sleep entries (today/yesterday), the end date must be today or the same day as the start date."
                )
            }
        } else {
            if (startDate.isAfter(nowDate)) {
                throw BadRequestException("Historical sleep log start date cannot be in the future.")
            }

            if (endDate.isAfter(nowDate)) {
                throw BadRequestException("Historical sleep log end date cannot be in the future.")
            }

            if (endDate.isAfter(startDate.plusDays(1))) {
                throw BadRequestException(
                    "Historical sleep session cannot span more than two days (e.g., bedtime Jan 1st, wake-up Jan 2nd is max)."
                )
            }
        }
    }
}

