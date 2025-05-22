package com.noom.interview.fullstack.sleep.domain.json.dto

import java.time.Instant

data class UserDTO (
        val idUser: String,
        val username: String,
        val dateCreate: Instant
)
