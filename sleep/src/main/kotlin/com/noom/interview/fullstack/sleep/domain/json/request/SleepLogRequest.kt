package com.noom.interview.fullstack.sleep.domain.json.request

data class SleepLogRequest (
    val idUser: String = "",
    val dateSleep: String = "",
    val dateBedtimeStart: String = "",
    val dateBedtimeEnd: String = "",
    val feelingMorning: String = ""
)

