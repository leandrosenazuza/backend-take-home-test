package com.noom.interview.fullstack.sleep.domain.json.response

data class SleepLogAvgLastThirtyDaysResponse(
    val idUser: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val averageTotalTimeInBed: Double = 0.0,
    val averageDateBedtimeStart: String = "",
    val averageDateBedtimeEnd: String = "",
    val qtdDaysGood: Int = 0,
    val qtdDaysBad: Int = 0,
    val qtdDaysOk: Int = 0,
)
