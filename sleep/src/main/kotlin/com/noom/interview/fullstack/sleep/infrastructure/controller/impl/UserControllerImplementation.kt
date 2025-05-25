package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.noom.interview.fullstack.sleep.domain.constants.*
import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
import com.noom.interview.fullstack.sleep.infrastructure.controller.UserController
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

@RestController
@Validated
class UserControllerImplementation(@Autowired val userUserCase: UserUseCase) : UserController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping(URI_GET_USER_V1)
    override fun getUserById(@PathVariable("idUser") idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>>
     {
        logger.info("Request to get user by id: + $idUser")
        val response = userUserCase.getUser(idUser)
        return ResponseEntity.ok(response)
    }

    @PostMapping(URI_POST_USER_V1)
    override fun createUser(@RequestBody userRequest: UserRequest): ResponseEntity<ApiResponse<UserResponse?, Meta>> {
        logger.info("Request to POST user by body: + $userRequest")
        val response = userUserCase.createUser(userRequest)
        return ResponseEntity.created(URI(URI_POST_USER_V1)).body(response)
    }

    @PutMapping(URI_PUT_USER_V1)
    override fun updateUser(@RequestBody userRequest: UserRequest, @PathVariable idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>> {
        logger.info("Request to PUT user by body: + $userRequest")
        val response = userUserCase.updateUser(userRequest, idUser)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping(URI_DELETE_USER_V1)
    override fun deleteUserById(@PathVariable idUser: String): ResponseEntity<ApiResponse<UserResponse?, Meta>> {
        logger.info("Request to DELETE user by id: + $idUser")
        val user = userUserCase.deleteUser(idUser)
        return ResponseEntity.ok(user)
    }
}