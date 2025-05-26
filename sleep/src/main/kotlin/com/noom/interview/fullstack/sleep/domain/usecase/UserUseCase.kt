package com.noom.interview.fullstack.sleep.domain.usecase

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta

interface UserUseCase {
    fun getUser(idUser: String): ApiResponse<UserResponse?, Meta>
    fun createUser(userRequest: UserRequest): ApiResponse<UserResponse?, Meta>
    fun updateUser(userRequest: UserRequest, idUser: String): ApiResponse<UserResponse?, Meta>
    fun deleteUser(idUser: String): ApiResponse<UserResponse?, Meta>
    fun getUserById(idUser: String): User?
}