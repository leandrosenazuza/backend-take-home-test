package com.noom.interview.fullstack.sleep.helper.model

import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import java.time.Instant
import java.util.*

fun createUserEntityMock(
    idUser: String = UUID.randomUUID().toString(),
    username: String = "Jon Doe",
    dateCreate: Instant = getDateNowByServerMachine()
) = User(
    idUser = idUser,
    username = username,
    dateCreate = dateCreate
)