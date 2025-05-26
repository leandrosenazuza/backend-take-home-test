package com.noom.interview.fullstack.sleep.domain.model

import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tab_user")
data class User(
    @Id @Column(name = "id_user", nullable = false)
    val idUser: String = UUID.randomUUID().toString(),

    @Column(name = "val_user_name", nullable = false)
    val username: String,

    @Column(name = "dat_create", nullable = false)
    val dateCreate: Instant = getDateNowByServerMachine(),
)
