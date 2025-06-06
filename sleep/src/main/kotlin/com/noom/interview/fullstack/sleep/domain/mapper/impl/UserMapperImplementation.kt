package com.noom.interview.fullstack.sleep.domain.mapper.impl

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.mapper.UserMapper
import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserMapperImplementation : UserMapper {
    override fun toUserFromRequest(userRequest: UserRequest): User {
        return User(
            idUser = UUID.randomUUID().toString(),
            username = userRequest.userName,
            dateCreate = getDateNowByServerMachine(),
        )
    }

    override fun toUpdateUserFromRequest(userRequest: UserRequest, user: User): User {
        return User(
            idUser = user.idUser,
            username = userRequest.userName,
            dateCreate = user.dateCreate
        )
    }

    override fun toResponseFromUser(user: User): UserResponse {
        return UserResponse(
            idUser = user.idUser,
            userName = user.username
        )
    }
}