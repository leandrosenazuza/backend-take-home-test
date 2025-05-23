package com.noom.interview.fullstack.sleep.domain.json.response

data class SleepLogResponse (
    val idSleep: String = "",
    val idUser: String = "",
    val dateSleep: String = "",
    val dateBedtimeStart: String,
    val dateBedtimeEnd: String,
    val totalTimeInBedMinutes: Double,
    val feelingMorning: String = ""
)
