package com.noom.interview.fullstack.sleep.domain.json.response

data class SleepLogAvgLastThirtyDaysResponse(
    val idUser: String = "",
    val intervalOfTimeFormatted: String = "",
    var averageTotalTimeInBedFormatted: String = "",
    var averageDateBedtimeStartAndEndFormatted: String = "",
    val qtdDaysGood: Int = 0,
    val qtdDaysBad: Int = 0,
    val qtdDaysOk: Int = 0,
)
