package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.domain.constants.*
import com.noom.interview.fullstack.sleep.domain.json.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.helper.model.createSleepLogEntityMock
import com.noom.interview.fullstack.sleep.helper.model.createUserEntityMock
import com.noom.interview.fullstack.sleep.helper.request.createSleepLogRequestMock
import com.noom.interview.fullstack.sleep.infrastructure.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Tag("SleepLogIntegrationTest")
class SleepLogControllerImplementationTest : AbstractTest() {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var sleepLogRepository: SleepLogRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        sleepLogRepository.deleteAll()
    }

    @Test
    fun `Should successfully delete a sleep log`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)

        userRepository.save(user)

        val zoneId = getZoneId()
        val currentDate = LocalDate.now(zoneId)
        val bedtimeStart = localDateTimeToInstant(currentDate.minusDays(1), 22, 0)
        val bedtimeEnd = localDateTimeToInstant(currentDate, 7, 0)
        val totalTimeInBed = getDifferenceOfTime(bedtimeStart, bedtimeEnd)

        val sleepLog = createSleepLogEntityMock(
            idUser = idUser,
            dateBedtimeStart = bedtimeStart,
            dateBedtimeEnd = bedtimeEnd,
            totalTimeInBedMinutes = totalTimeInBed
        )

        val savedSleepLog = sleepLogRepository.save(sleepLog)

        mockMvc.perform(
            MockMvcRequestBuilders.delete(URI_DELETE_SLEEP_LOG_V1.replace("{idSleep}", savedSleepLog.idSleep))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateSleep").value(formatTodayDate(sleepLog.dateSleep)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value("2025-05-25T01:00:00Z"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value("2025-05-25T10:00:00Z"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedFormatted")
                    .value(formatTimeInBed(sleepLog.totalTimeInBedMinutes))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStartAndEndFormatted").value(
                    formatStartAndEndInterval(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
                )
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedMinutes").value("540.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idSleep").value(savedSleepLog.idSleep))
            .andDo(MockMvcResultHandlers.print())

        val resultSleepLog = sleepLogRepository.findById(savedSleepLog.idSleep)
        assertTrue(resultSleepLog.isEmpty, "SleepLog deleted with success!")
    }

    @Test
    fun `Should successfully get last night sleep log information`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)
        userRepository.save(user)

        val nowDate = LocalDate.now(getZoneId())
        val yesterday = nowDate.minusDays(1)
        val bedtimeStart = localDateTimeToInstant(yesterday, 22, 0)
        val bedtimeEnd = localDateTimeToInstant(nowDate, 7, 31)
        val averageMinutesInBed = getDifferenceOfTime(bedtimeStart, bedtimeEnd)

        val sleepLog = createSleepLogEntityMock(
            idUser = idUser,
            dateBedtimeStart = bedtimeStart,
            dateBedtimeEnd = bedtimeEnd,
            totalTimeInBedMinutes = averageMinutesInBed
        )
        sleepLogRepository.save(sleepLog)

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedMinutes").value(averageMinutesInBed))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateSleep").value(formatTodayDate(sleepLog.dateSleep)))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedFormatted")
                    .value(formatTimeInBed(sleepLog.totalTimeInBedMinutes))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value(bedtimeStart.toString()))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStartAndEndFormatted").value(
                    formatStartAndEndInterval(sleepLog.dateBedtimeStart, sleepLog.dateBedtimeEnd)
                )
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value(bedtimeEnd.toString()))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feelingMorning")
                    .value(MorningFeelingEnum.fromString(sleepLog.feelingMorning)?.displayName)
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should return NOT FOUND when user has no sleep log for last night`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)
        userRepository.save(user)

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should return 404 when user does not exist`() {
        val nonExistentUserId = "some-non-existent-user"

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1.replace("{idUser}", nonExistentUserId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `Should successfully create a new sleep log`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)
        userRepository.save(user)

        val nowDate = LocalDate.now(getZoneId())
        val yesterday = nowDate.minusDays(1)
        val bedtimeStart = localDateTimeToInstant(yesterday, 22, 0)
        val bedtimeEnd = localDateTimeToInstant(nowDate, 7, 31)
        val averageMinutesInBed = getDifferenceOfTime(bedtimeStart, bedtimeEnd)
        val sleepLogRequest = createSleepLogRequestMock(
            idUser = idUser,
            bedtimeStart = bedtimeStart.toString(),
            bedtimeEnd = bedtimeEnd.toString()
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post(URI_POST_SLEEP_LOG_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(sleepLogRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedMinutes").value(averageMinutesInBed))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value(bedtimeStart.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value(bedtimeEnd.toString()))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feelingMorning")
                    .value(MorningFeelingEnum.fromString(sleepLogRequest.feelingMorning)?.displayName)
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should successfully update a sleep log in the past`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)
        userRepository.save(user)

        val oldDateSleep = LocalDate.now(getZoneId()).minusMonths(1)
        val oldBedtimeStart = oldDateSleep.minusDays(1).atTime(23, 0).atZone(getZoneId()).toInstant()
        val oldBedtimeEnd = oldDateSleep.atTime(8, 0).atZone(getZoneId()).toInstant()
        val originalTotalTimeInBed = getDifferenceOfTime(oldBedtimeStart, oldBedtimeEnd)

        val existingSleepLog = createSleepLogEntityMock(
            idUser = idUser,
            dateSleep = oldDateSleep.atStartOfDay(getZoneId()).toInstant(),
            dateBedtimeStart = oldBedtimeStart,
            dateBedtimeEnd = oldBedtimeEnd,
            totalTimeInBedMinutes = originalTotalTimeInBed,
            feelingMorning = "OK"
        )
        val savedSleepLog = sleepLogRepository.save(existingSleepLog)

        val updatedFeeling = "GOOD"
        val updatedBedtimeStart = oldDateSleep.minusDays(1).atTime(22, 30).atZone(getZoneId()).toInstant()
        val updatedBedtimeEnd = oldDateSleep.atTime(7, 30).atZone(getZoneId()).toInstant()
        val updatedTotalTimeInBed = getDifferenceOfTime(updatedBedtimeStart, updatedBedtimeEnd)

        val sleepLogRequest = createSleepLogRequestMock(
            idUser = idUser,
            dateSleep = oldDateSleep,
            bedtimeStart = updatedBedtimeStart.toString(),
            bedtimeEnd = updatedBedtimeEnd.toString(),
            feelingMorning = updatedFeeling
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put(URI_PUT_SLEEP_LOG_V1.replace("{idSleep}", savedSleepLog.idSleep))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    (ObjectMapper().writeValueAsString(sleepLogRequest))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idSleep").value(savedSleepLog.idSleep))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.dateSleep")
                    .value(formatTodayDate(oldDateSleep.atStartOfDay(getZoneId()).toInstant()))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value(updatedBedtimeStart.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value(updatedBedtimeEnd.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalTimeInBedMinutes").value(updatedTotalTimeInBed))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feelingMorning").value(MorningFeelingEnum.GOOD.displayName)
            )
            .andDo(MockMvcResultHandlers.print())

        val updatedSleepLog = sleepLogRepository.findById(savedSleepLog.idSleep).orElse(null)
        assertEquals(savedSleepLog.idSleep, updatedSleepLog.idSleep)
        assertEquals(updatedFeeling, updatedSleepLog.feelingMorning)
        assertEquals(updatedBedtimeStart, updatedSleepLog.dateBedtimeStart)
        assertEquals(updatedBedtimeEnd, updatedSleepLog.dateBedtimeEnd)
        assertEquals(updatedTotalTimeInBed, updatedSleepLog.totalTimeInBedMinutes)
    }

    @Test
    fun `Should respond OK for a sleep log paginated of 60 sleep log registers`() {
        val idUser = UUID.randomUUID().toString()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..60) {
            val currentDate = LocalDate.now(getZoneId()).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)

            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(getZoneId()).toInstant(),
                dateBedtimeStart = previousDate.atTime(22, 0).atZone(getZoneId()).toInstant(),
                dateBedtimeEnd = currentDate.atTime(7, 0).atZone(getZoneId()).toInstant(),
                totalTimeInBedMinutes = 540.0,
                feelingMorning = "GOOD"
            )
            sleepLogRepository.save(sleepLog)
        }

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_ALL_DAYS_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "1")
                .queryParam("page-size", "25")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(25))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].idSleep").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].dateSleep").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].dateBedtimeStart").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].dateBedtimeEnd").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].totalTimeInBedMinutes").value(540.0))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data[0].feelingMorning").value(MorningFeelingEnum.GOOD.displayName)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.totalRecords").value(61))
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.totalPages").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.requestDateTime").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should respond with the correct average time interval for the last 30 days of sleep log with same time interval`() {
        val idUser = UUID.randomUUID().toString()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..60) {
            val currentDate = LocalDate.now(getZoneId()).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)

            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(getZoneId()).toInstant(),
                dateBedtimeStart = previousDate.atTime(22, 0).atZone(getZoneId()).toInstant(),
                dateBedtimeEnd = currentDate.atTime(7, 0).atZone(getZoneId()).toInstant(),
                totalTimeInBedMinutes = 540.0,
                feelingMorning = "GOOD"
            )
            sleepLogRepository.save(sleepLog)
        }

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("The sleep log average of the last 30 days return with success!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.averageTotalTimeInBedFormatted").value("15 h 00 min"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.averageDateBedtimeStartAndEndFormatted")
                    .value("10:00 pm - 7:00 am")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysGood").value(30))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysBad").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysOk").value(0))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should respond with the correct average time interval for the last 30 days of sleep log with little difference in the interval of time`() {
        val idUser = UUID.randomUUID().toString()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..29) {
            val currentDate = LocalDate.now(getZoneId()).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)
            val startDate = previousDate.atTime(22, i).atZone(getZoneId()).toInstant()
            val endDate = currentDate.atTime(7, i).atZone(getZoneId()).toInstant()
            val totalTime = getDifferenceOfTime(startDate, endDate)
            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(getZoneId()).toInstant(),
                dateBedtimeStart = startDate,
                dateBedtimeEnd = endDate,
                totalTimeInBedMinutes = totalTime,
                feelingMorning = randonMorningEnum(i)
            )
            sleepLogRepository.save(sleepLog)
        }

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("The sleep log average of the last 30 days return with success!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.averageTotalTimeInBedFormatted").value("15 h 00 min"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.averageDateBedtimeStartAndEndFormatted")
                    .value("10:14 pm - 7:14 am")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysGood").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysBad").value(12))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysOk").value(13))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should respond with the correct average time interval for the last 30 days of sleep log with natural interval of time`() {
        val idUser = UUID.randomUUID().toString()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..29) {
            val currentDate = LocalDate.now(getZoneId()).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)
            val startDate = previousDate.atTime(18 + randonHourStart(i), i).atZone(getZoneId()).toInstant()
            val endDate = currentDate.atTime(4 + randonHourEnd(i), i).atZone(getZoneId()).toInstant()
            val totalTime = getDifferenceOfTime(startDate, endDate)
            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(getZoneId()).toInstant(),
                dateBedtimeStart = startDate,
                dateBedtimeEnd = endDate,
                totalTimeInBedMinutes = totalTime,
                feelingMorning = randonMorningEnum(i)
            )
            sleepLogRepository.save(sleepLog)
        }

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("The sleep log average of the last 30 days return with success!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.averageTotalTimeInBedFormatted").value("14 h 02 min"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.averageDateBedtimeStartAndEndFormatted")
                    .value("6:26 pm - 4:24 am")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysGood").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysBad").value(12))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysOk").value(13))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should respond with the correct average time interval for the last 30 days of sleep log with a lot of start dates on the same day of today`() {
        val idUser = UUID.randomUUID().toString()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..29) {
            val currentDate = LocalDate.now(getZoneId()).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)
            val startDate = if (i % 10 == 0) currentDate.atTime(0 + randonHourStart(i), i).atZone(getZoneId()).toInstant()
            else previousDate.atTime(20 + randonHourStart(i), i).atZone(getZoneId()).toInstant()
            val endDate = currentDate.atTime(4 + randonHourEnd(i), i).atZone(getZoneId()).toInstant()
            val totalTime = getDifferenceOfTime(startDate, endDate)
            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(getZoneId()).toInstant(),
                dateBedtimeStart = startDate,
                dateBedtimeEnd = endDate,
                totalTimeInBedMinutes = totalTime,
                feelingMorning = randonMorningEnum(i)
            )
            sleepLogRepository.save(sleepLog)
        }

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("The sleep log average of the last 30 days return with success!")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.averageTotalTimeInBedFormatted").value("14 h 02 min"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.averageDateBedtimeStartAndEndFormatted")
                    .value("6:26 pm - 4:24 am")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysGood").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysBad").value(12))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.qtdDaysOk").value(13))
            .andDo(MockMvcResultHandlers.print())
    }

    fun randonHourStart(i: Int): Int {
        if(i % 5 == 0) return 1
        else return 0
    }
    fun randonHourEnd(i: Int): Int {
        if(i % 2 == 0 && i % 3 == 0) return 1
        else return 0
    }

    fun randonMorningEnum(i: Int): String {
        if(i == 0) return "OK"
        else if(i % 5 == 0) return "GOOD"
        else if(i % 2 == 0) return "BAD"
        else return "OK"
    }
}