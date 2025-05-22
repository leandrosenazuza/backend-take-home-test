package com.noom.interview.fullstack.sleep.infrastructure.controller

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import org.springframework.http.ResponseEntity

interface UserController {
    fun getUserById(userId: String): ResponseEntity<UserResponse>?
    fun createUser(userRequest: UserRequest): ResponseEntity<UserResponse>
    fun updateUser(userRequest: UserRequest, userId: String): ResponseEntity<UserResponse>?
    fun deleteUserById(userId: String): ResponseEntity<String>?
}