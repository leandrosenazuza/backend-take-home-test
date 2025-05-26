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
import java.time.format.TextStyle
import java.util.*
import kotlin.streams.toList

@Service
class SleepLogUseCaseImplementation(
    @Autowired val sleepLogRepository: SleepLogRepository,
    @Autowired val sleepLogMapper: SleepLogMapper,
    @Autowired val userUseCase: UserUseCase
) : SleepLogUseCase {

    override fun createSleepLog(sleepLogRequest: SleepLogRequest): ApiResponse<SleepLogResponse?, Meta> {
        validateTodayDate(sleepLogRequest)
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
        validateAnyDate(sleepLogRequest)
        validateRequest(sleepLogRequest)
        val sleepLog = this.getSleepLog(idSleep)
        if (sleepLog != null) {
            val sleepLogUpdated: SleepLog = sleepLogMapper.toUpdateSleepLogFromRequest(sleepLogRequest, sleepLog)
            sleepLogUpdated.totalTimeInBedMinutes =
                getDifferenceOfTime(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
            val sleepLogNew = sleepLogRepository.save(sleepLogUpdated)
            val data: SleepLogResponse = sleepLogMapper.toResponseFromSleepLog(sleepLogNew)
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
                intervalOfTimeFormatted = getIntervalOfTimeFormatted(),
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

            data.averageTotalTimeInBedFormatted = formatDurationList(
                Duration.between(
                    getAvgTimeHourMinuteSecondStart(sleepLogList),
                    getAvgTimeHourMinuteSecondEnd(sleepLogList)
                )
            )

            return ApiResponse.Builder<SleepLogAvgLastThirtyDaysResponse, Meta>()
                .status("success")
                .data(data)
                .message("The sleep log average of the last 30 days return with success!")
                .build()
        } else throw NotFoundException()
    }

    fun formatDurationList(duration: Duration): String {
        val absDuration = duration.abs()
        val hours = absDuration.toHours()
        val minutes = absDuration.toMinutes() % 60
        return "%d h %02d min".format(hours, minutes)
    }

    fun formatStartAndEndInterval(start: Instant, end: Instant): String {
        val formatter = DateTimeFormatter
            .ofPattern("h:mm a", Locale.ENGLISH)
            .withZone(getZoneId())

        val formattedStart = formatter.format(start).replace("AM", "am").replace("PM", "pm")
        val formattedEnd = formatter.format(end).replace("AM", "am").replace("PM", "pm")

        return "$formattedStart - $formattedEnd"
    }

    fun getDayWithSuffix(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    fun getIntervalOfTimeFormatted(): String {
        val today = LocalDate.now()
        val thirtyDaysAgo = today.minusDays(29)

        val startMonth = thirtyDaysAgo.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        val endMonth = today.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        val startDay = getDayWithSuffix(thirtyDaysAgo.dayOfMonth)
        val endDay = getDayWithSuffix(today.dayOfMonth)

        return "$startMonth $startDay to $endMonth $endDay"
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

    private fun getQuantityOfMood(mood: String, sleepLogList: List<SleepLog>): Int {
        var quantity = 0
        for (item in sleepLogList) {
            if (item.feelingMorning == mood) ++quantity
        }
        return quantity
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
        validateDatesPattern(sleepLogRequest)
        validateUser(sleepLogRequest.idUser)
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

    private fun validateUser(idUser: String) {
        if (userUseCase.getUserById(idUser) == null) {
            throw BadRequestException()
        }
    }

    fun validateAnyDate(sleepLogRequest: SleepLogRequest) {
        val dateSleepParsed: LocalDate = LocalDate.parse(sleepLogRequest.dateSleep)
        val startInstant: Instant = Instant.parse(sleepLogRequest.dateBedtimeStart)
        val endInstant: Instant = Instant.parse(sleepLogRequest.dateBedtimeEnd)
        val startDateFromInstant: LocalDate = startInstant.atZone(getZoneId()).toLocalDate()
        val endDateFromInstant: LocalDate = endInstant.atZone(getZoneId()).toLocalDate()


        if (endDateFromInstant != dateSleepParsed) {
            throw BadRequestException()
        }

        val oneDayBeforeDateSleep = dateSleepParsed.minusDays(1)

        if (startDateFromInstant != dateSleepParsed && startDateFromInstant != oneDayBeforeDateSleep) {
            throw BadRequestException()
        }

        if (startInstant.isAfter(endInstant)) {
            throw BadRequestException()
        }
    }


    private fun validateTodayDate(sleepLogRequest: SleepLogRequest) {
        if (!isToday(sleepLogRequest.dateSleep)) {
            throw BadRequestException()
        }

        val dateSleepParsed: LocalDate = LocalDate.parse(sleepLogRequest.dateSleep)
        val startDateParsed: LocalDate = LocalDate.parse(sleepLogRequest.dateBedtimeStart.substringBefore('T'))
        val endDateParsed: LocalDate = LocalDate.parse(sleepLogRequest.dateBedtimeEnd.substringBefore('T'))

        if (endDateParsed != dateSleepParsed) {
            throw BadRequestException()
        }

        val oneDayBeforeDateSleep = dateSleepParsed.minusDays(1)

        if (startDateParsed != dateSleepParsed && startDateParsed != oneDayBeforeDateSleep) {
            throw BadRequestException()
        }
    }

    private fun isToday(targetInstant: String): Boolean {
        val todayLocalDate = ZonedDateTime.ofInstant(getDateNowByServerMachine(), getZoneId()).toLocalDate().toString()
        return todayLocalDate == targetInstant
    }


    fun validateDatesPattern(sleepLogRequest: SleepLogRequest) {
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



