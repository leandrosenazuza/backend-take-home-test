package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.domain.constants.URI_GET_ALL_DAYS_SLEEP_BY_ID_USER_V1
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.helper.model.createSleepLogEntityMock
import com.noom.interview.fullstack.sleep.helper.model.createUserEntityMock
import com.noom.interview.fullstack.sleep.helper.request.createSleepLogRequestMock
import com.noom.interview.fullstack.sleep.infrastructure.util.getZoneId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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