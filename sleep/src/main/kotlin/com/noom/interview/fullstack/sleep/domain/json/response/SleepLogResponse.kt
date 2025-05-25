package com.noom.interview.fullstack.sleep.domain.json.response

data class SleepLogResponse (
    val idSleep: String = "",
    val idUser: String = "",
    val dateSleep: String = "",
    val dateBedtimeStart: String,
    val dateBedtimeEnd: String,
    val dateBedtimeStartAndEnd: String = "",
    val totalTimeInBedMinutes: Double,
    val totalTimeInBedFormated: String = "0.0",
    val feelingMorning: String = ""
)
