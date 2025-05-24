package com.noom.interview.fullstack.sleep.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "tab_user")
data class User(
    @Id @Column(name = "id_user", nullable = false)
    val idUser: String = UUID.randomUUID().toString(),

    @Column(name = "val_user_name", nullable = false)
    val username: String,

    @Column(name = "dat_create", nullable = false)
    val dateCreate: Instant = ZonedDateTime.now(ZoneId.systemDefault()).toInstant(),
)
