package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.noom.interview.fullstack.sleep.domain.constants.*
import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
import com.noom.interview.fullstack.sleep.infrastructure.controller.UserController
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class UserControllerImplementation(@Autowired val userUserCase: UserUseCase) : UserController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping(URI_GET_USER_V1)
    override fun getUserById(@PathVariable("userId") userId: String): ResponseEntity<UserResponse>?
     {
        logger.info("Request to get user by id: + $userId")
        val response = userUserCase.getUser(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping(URI_POST_USER_V1)
    override fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
        logger.info("Request to POST user by body: + $userRequest")
        val response = userUserCase.createUser(userRequest)
        return ResponseEntity.ok(response)
    }

    @PutMapping(URI_PUT_USER_V1)
    override fun updateUser(@RequestBody userRequest: UserRequest, @PathVariable userId: String): ResponseEntity<UserResponse>? {
        logger.info("Request to PUT user by body: + $userRequest")
        val response = userUserCase.updateUser(userRequest, userId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping(URI_DELETE_USER_V1)
    override fun deleteUserById(@PathVariable userId: String): ResponseEntity<String>? {
        logger.info("Request to DELETE user by id: + $userId")
        userUserCase.deleteUser(userId)
        return ResponseEntity.ok("Deleted user with id: $userId")
    }
}