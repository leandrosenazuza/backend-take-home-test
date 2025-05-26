package com.noom.interview.fullstack.sleep.domain.model

import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tab_sleep_log")
data class SleepLog(

    @Id @Column(name = "id_sleep", nullable = false)
    val idSleep: String = UUID.randomUUID().toString(),

    @Column(name = "id_user", nullable = false)
    val idUser: String = "",

    @Column(name = "dat_sleep_date", nullable = false)
    val dateSleep: Instant,

    @Column(name = "dat_bed_time_start", nullable = false)
    val dateBedtimeStart: Instant,

    @Column(name = "dat_bed_time_end", nullable = false)
    val dateBedtimeEnd: Instant,

    @Column(name = "val_total_time_bed_minutes")
    var totalTimeInBedMinutes: Double = 0.0,

    @Column(name = "ind_feeling_morning", nullable = false)
    val feelingMorning: String = "",

    @Column(name = "dat_create")
    val dateCreate: Instant = getDateNowByServerMachine()
)
