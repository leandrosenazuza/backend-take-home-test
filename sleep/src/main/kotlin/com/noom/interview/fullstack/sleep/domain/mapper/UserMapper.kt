package com.noom.interview.fullstack.sleep.domain.mapper

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.model.User

interface UserMapper {
    fun toUserFromRequest(userRequest: UserRequest): User
    fun toUpdateUserFromRequest(userRequest: UserRequest, user: User): User
    fun toResponseFromUser(user: User): UserResponse
}

