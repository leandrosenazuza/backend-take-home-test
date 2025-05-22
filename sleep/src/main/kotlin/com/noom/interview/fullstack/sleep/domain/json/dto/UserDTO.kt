package com.noom.interview.fullstack.sleep.domain.model.dto

import java.time.Instant

data class UserDTO (
        val idUser: String,
        val username: String,
        val dateCreate: Instant
    )
