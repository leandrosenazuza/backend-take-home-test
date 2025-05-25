package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.domain.constants.URI_DELETE_SLEEP_LOG_V1
import com.noom.interview.fullstack.sleep.domain.constants.URI_GET_ALL_DAYS_SLEEP_BY_ID_USER_V1
import com.noom.interview.fullstack.sleep.domain.constants.URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1
import com.noom.interview.fullstack.sleep.domain.constants.URI_POST_SLEEP_LOG_V1
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.helper.model.createSleepLogEntityMock
import com.noom.interview.fullstack.sleep.helper.model.createUserEntityMock
import com.noom.interview.fullstack.sleep.helper.request.createSleepLogRequestMock
import com.noom.interview.fullstack.sleep.infrastructure.util.getDifferenceOfTime
import com.noom.interview.fullstack.sleep.infrastructure.util.getZoneId
import com.noom.interview.fullstack.sleep.infrastructure.util.localDateTimeToInstant
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
import java.util.*

@Tag("IntegrationTest")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateSleep").value("2025-05-25"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value("2025-05-25T01:00:00Z"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value("2025-05-25T10:00:00Z"))
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeStart").value(bedtimeStart.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateBedtimeEnd").value(bedtimeEnd.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feelingMorning").value(sleepLog.feelingMorning))
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feelingMorning").value(sleepLogRequest.feelingMorning))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should respond OK for a sleep log paginated of 60 sleep log registers`() {
        val idUser = UUID.randomUUID().toString()
        val zoneId = getZoneId()

        userRepository.save(createUserEntityMock(idUser = idUser))

        for (i in 0..60) {
            val currentDate = LocalDate.now(zoneId).minusDays(i.toLong())
            val previousDate = currentDate.minusDays(1)

            val sleepLog = SleepLog(
                idUser = idUser,
                dateSleep = currentDate.atStartOfDay(zoneId).toInstant(),
                dateBedtimeStart = previousDate.atTime(22, 0).atZone(zoneId).toInstant(),
                dateBedtimeEnd = currentDate.atTime(7, 0).atZone(zoneId).toInstant(),
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].feelingMorning").value("GOOD"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.totalRecords").value(61))
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.totalPages").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.meta.requestDateTime").exists())
            .andDo(MockMvcResultHandlers.print())
    }
}