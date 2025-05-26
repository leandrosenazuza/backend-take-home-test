package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.helper.model.createSleepLogEntityMock
import com.noom.interview.fullstack.sleep.helper.request.createSleepLogRequestMock
import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import com.noom.interview.fullstack.sleep.infrastructure.util.getZoneId
import com.noom.interview.fullstack.sleep.infrastructure.util.localDateTimeToInstant
import com.noom.interview.fullstack.sleep.infrastructure.util.parseStringToInstant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Tag("UnitTest")
class SleepLogUseCaseImplementationTest : AbstractTest() {

    @Autowired
    private lateinit var sleepLogUseCaseImpl: SleepLogUseCaseImplementation

    @Autowired
    private lateinit var sleepLogRepository: SleepLogRepository

    @BeforeEach
    fun setUp() {
        sleepLogRepository.deleteAll()
    }

    @Test
    fun `Should calculate correct average start time`() {
        val sleepLogs = listOf(
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-01T22:00:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-02T06:00:00Z"),
            ),
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-02T22:15:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-03T06:15:00Z")
            ),
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-03T22:30:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-04T06:30:00Z")
            ))

        val avgStart = sleepLogUseCaseImpl.getAvgTimeHourMinuteSecondStart(sleepLogs).toString()

        assertEquals("22:15:00Z",  avgStart.split("T")[1])
    }

    @Test
    fun `Should calculate correct average end time not considerin the date, moth, year, only the hour that started to sleep and wake up`() {
        val sleepLogs = listOf(
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-01T22:00:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-02T06:00:00Z"),
            ),
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-02T22:15:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-03T06:15:00Z")
            ),
            createSleepLogEntityMock(
                dateBedtimeStart = parseStringToInstant("2023-10-03T22:30:00Z"),
                dateBedtimeEnd = parseStringToInstant("2023-10-04T06:30:00Z")
            ))

        val avgEnd = sleepLogUseCaseImpl.getAvgTimeHourMinuteSecondEnd(sleepLogs).toString()


        assertEquals("06:15:00Z",  avgEnd.split("T")[1])
    }

    @Test
    fun `Should not throw any exception for a valid sleep log with bedtimeStart yesterday, end bedtimeEnd`() {
        val nowDate = LocalDate.now(getZoneId())
        val yesterday = nowDate.minusDays(1)

        val request = createSleepLogRequestMock(
            bedtimeStart = localDateTimeToInstant(yesterday, 22, 0).toString(),
            bedtimeEnd = localDateTimeToInstant(nowDate, 7, 30).toString()
        )

        assertDoesNotThrow { sleepLogUseCaseImpl.validateDatesPattern(request) }
    }

    @Test
    fun `Should return BadRequestException when bedtimeEnd before bedtimeStart`() {
        val nowDate = LocalDate.now(getZoneId())

        val request = createSleepLogRequestMock(
            bedtimeStart = localDateTimeToInstant(nowDate, 8, 0).toString(),
            bedtimeEnd = localDateTimeToInstant(nowDate, 7, 0).toString()
        )

        assertThrows(BadRequestException::class.java) {
            sleepLogUseCaseImpl.validateAnyDate(request)
        }
    }

    @Test
    fun `Should return BadRequestException when bedtimeStart is more than 1 day older of bedtimeEnd`() {
        val nowDate = LocalDate.now(getZoneId())
        val twoDaysAgo = nowDate.minusDays(2)

        val request = createSleepLogRequestMock(
            bedtimeStart = localDateTimeToInstant(twoDaysAgo, 22, 0).toString(),
            bedtimeEnd = localDateTimeToInstant(nowDate, 7, 0).toString()
        )

        assertThrows(BadRequestException::class.java) {
            sleepLogUseCaseImpl.validateAnyDate(request)
        }
    }

    @Test
    fun `Should not throw any exception for a valid sleep log with bedtimeStart and bedtimeEnd`() {
        val nowDate = LocalDate.now(getZoneId())

        val request = createSleepLogRequestMock(
            bedtimeStart = localDateTimeToInstant(nowDate, 1, 0).toString(),
            bedtimeEnd = localDateTimeToInstant(nowDate, 9, 0).toString(),
        )

        assertDoesNotThrow { sleepLogUseCaseImpl.validateDatesPattern(request) }
    }

    @Test
    fun `Should return BadRequestException when the date format is invalid`() {
        val nowDate = LocalDate.now(getZoneId())

        val request = createSleepLogRequestMock(
            bedtimeStart = "not-a-date",
            bedtimeEnd = "not-a-date",
        )

        assertThrows(BadRequestException::class.java) {
            sleepLogUseCaseImpl.validateDatesPattern(request)
        }
    }

    @Test
    fun `Should return BadRequestException when bedtimeEnd is not the date of today or even the bedtimeStart`() {
        val nowDate = LocalDate.now(getZoneId())

        val request = createSleepLogRequestMock(
            bedtimeStart = localDateTimeToInstant(nowDate, 2, 0).toString(),
            bedtimeEnd = localDateTimeToInstant(nowDate.plusDays(1), 2, 0).toString(),
        )

        assertThrows(BadRequestException::class.java) {
            sleepLogUseCaseImpl.validateAnyDate(request)
        }
    }
}
