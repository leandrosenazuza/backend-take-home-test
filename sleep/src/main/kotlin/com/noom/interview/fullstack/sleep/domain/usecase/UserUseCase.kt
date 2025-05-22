package com.noom.interview.fullstack.sleep.domain.usecase

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.model.User

interface UserUseCase {
    fun getUser(idUser: String): UserResponse?
    fun createUser(userRequest: UserRequest): UserResponse
    fun updateUser(userRequest: UserRequest, userId: String): UserResponse
    fun deleteUser(idUser: String)
}