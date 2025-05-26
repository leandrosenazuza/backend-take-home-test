package com.noom.interview.fullstack.sleep.infrastructure.controller

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.springframework.http.ResponseEntity

interface UserController {
    fun getUserById(idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>>
    fun createUser(userRequest: UserRequest): ResponseEntity<ApiResponse<UserResponse?, Meta>>
    fun updateUser(userRequest: UserRequest, idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>>
    fun deleteUserById(idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>>
}