package com.noom.interview.fullstack.sleep.infrastructure.util

import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class TimeUtilsKtTest : AbstractTest(){

    private lateinit var originalTimeZone: TimeZone

    @BeforeEach
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @AfterEach
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `Should calculate correct duration for same-day times`() {
        val start = Instant.parse("2023-10-01T22:00:00Z")
        val end = Instant.parse("2023-10-01T23:00:00Z")

        val duration = calculateDuration(start, end)

        assertEquals(Duration.ofHours(1), duration)
    }

    @Test
    fun `Should calculate correct duration for overnight times`() {
        val start = Instant.parse("2023-10-01T22:00:00Z")
        val end = Instant.parse("2023-10-02T07:00:00Z")

        val duration = calculateDuration(start, end)

        assertEquals(Duration.ofHours(9), duration)
    }

    @Test
    fun `Should calculate correct duration when end time matches start time`() {
        val start = Instant.parse("2023-10-01T22:00:00Z")
        val end = Instant.parse("2023-10-01T22:00:00Z")

        val duration = calculateDuration(start, end)

        assertEquals(Duration.ZERO, duration)
    }

    @Test
    fun `Should calculate correct duration when times are reversed across midnight`() {
        val start = Instant.parse("2023-10-01T15:00:00Z")
        val end = Instant.parse("2023-10-02T03:00:00Z")

        val duration = calculateDuration(start, end)

        assertEquals(Duration.ofHours(12), duration)
    }

    @Test
    fun `getDifferenceOfTime returns positive minutes between instants`() {
        val start = Instant.parse("2024-01-01T00:00:00Z")
        val end   = Instant.parse("2024-01-01T02:30:00Z")
        assertEquals(150.0, getDifferenceOfTime(start, end))
    }

    @ParameterizedTest(name = "{0}  ->  \"{1}\"")
    @CsvSource(
        delimiter = '|', value = [
            "1  | 1st",
            "2  | 2nd",
            "3  | 3rd",

            "4  | 4th",
            "30 | 30th",

            "11 | 11th",
            "12 | 12th",
            "13 | 13th",

            "21 | 21st",
            "22 | 22nd",
            "23 | 23rd",

            "31 | 31st"
        ]
    )
    fun `getDayWithSuffix returns correct ordinal string`(day: Int, expected: String) {
        assertEquals(expected, getDayWithSuffix(day))
    }


    @Test
    fun `localDateTimeToInstant converts date plus hour and minute to instant in server zone`() {
        val date   = LocalDate.of(2024, 1, 1)
        val expect = Instant.parse("2024-01-01T08:15:00Z") // zone is UTC
        assertEquals(expect, localDateTimeToInstant(date, 8, 15))
    }

    @Test
    fun `parseStringToInstant supports basic yyyy-MM-dd`() {
        val isoMidnight = Instant.parse("2024-05-01T00:00:00Z")
        assertEquals(isoMidnight, parseStringToInstant("2024-05-01"))
    }

    @Test
    fun `parseStringToInstant supports ISO-8601`() {
        val instant = Instant.parse("2024-05-01T12:34:56Z")
        assertEquals(instant, parseStringToInstant("2024-05-01T12:34:56Z"))
    }

    @Test
    fun `parseStringToInstant throws BadRequestException on invalid input`() {
        assertThrows<BadRequestException> { parseStringToInstant("2024_05_01") }
    }

    @Test
    fun `formatTimeInBed formats hours and minutes correctly`() {
        assertEquals("1 h 30 min", formatTimeInBed(90.0))
        assertEquals("0 h 59 min", formatTimeInBed(59.0))
    }

    @ParameterizedTest(name = "{0}  ->  \"{1}\"")
    @CsvSource(
        delimiter = '|', value = [
            "2024-01-01T00:00:00Z | January, 1st",
            "2024-01-02T00:00:00Z | January, 2nd",
            "2024-01-03T00:00:00Z | January, 3rd",
            "2024-01-04T00:00:00Z | January, 4th",
            "2024-01-11T00:00:00Z | January, 11th",
            "2024-01-12T00:00:00Z | January, 12th",
            "2024-01-13T00:00:00Z | January, 13th",
            "2024-01-21T00:00:00Z | January, 21st",
            "2024-02-22T00:00:00Z | February, 22nd",
            "2024-03-23T00:00:00Z | March, 23rd",
            "2024-04-30T00:00:00Z | April, 30th"
        ]
    )
    fun `formatTodayDate produces correct english month and ordinal suffix`(
        instantIso: String,
        expected: String
    ) {
        assertEquals(expected, formatTodayDate(Instant.parse(instantIso)))
    }

    @Test
    fun `formatStartAndEndInterval converts to 12h clock with ampm suffix and dash`() {
        val start = Instant.parse("2024-01-01T22:00:00Z") // 10:00 pm UTC
        val end   = Instant.parse("2024-01-01T23:30:00Z") // 11:30 pm UTC
        assertEquals("10:00 pm - 11:30 pm", formatStartAndEndInterval(start, end))
    }

    @Test
    fun `formatDurationList prints absolute value with zero padded minutes`() {
        val pos  = Duration.ofMinutes(135)   // 2 h 15 min
        val neg  = Duration.ofMinutes(-5)    // 0 h 05 min
        assertEquals("2 h 15 min", formatDurationList(pos))
        assertEquals("0 h 05 min", formatDurationList(neg))
    }

    @Test
    fun `getZoneId returns system default zone`() {
        assertEquals(ZoneId.systemDefault(), getZoneId())
    }

    @Test
    fun `getDateNowByServerMachine is within one second of Instant_now`() {
        val before = Instant.now()
        val nowByServer = getDateNowByServerMachine()
        val after = Instant.now()
        assertTrue(!nowByServer.isBefore(before) && !nowByServer.isAfter(after.plusSeconds(1)))
    }

    @Test
    fun `getDateThirtyDaysLastByServerMachine is about thirty days ago`() {
        val now = Instant.now()
        val thirtyAgo = getDateThirtyDaysLastByServerMachine()

        val minutesDiff = ChronoUnit.MINUTES.between(thirtyAgo, now)
        assertTrue(minutesDiff in 43199..43201)
    }

    @Test
    fun `Zero duration is formatted as 0 h 00 min`() {
        val result = formatDurationList(Duration.ZERO)
        assertEquals("0 h 00 min", result)
    }

    @Test
    fun `One hour only`() {
        val result = formatDurationList(Duration.ofHours(1))
        assertEquals("1 h 00 min", result)
    }

    @Test
    fun `One hour five minutes`() {
        val result = formatDurationList(Duration.ofHours(1).plusMinutes(5))
        assertEquals("1 h 05 min", result)
    }

    @Test
    fun `Forty-five minutes`() {
        val result = formatDurationList(Duration.ofMinutes(45))
        assertEquals("0 h 45 min", result)
    }

    @Test
    fun `Two hours fifteen minutes`() {
        val result = formatDurationList(Duration.ofMinutes(135))
        assertEquals("2 h 15 min", result)
    }

    @Test
    fun `Negative duration is rendered as absolute value`() {
        val negative = Duration.ofHours(-2).minusMinutes(30)
        val result   = formatDurationList(negative)
        assertEquals("2 h 30 min", result)
    }
}