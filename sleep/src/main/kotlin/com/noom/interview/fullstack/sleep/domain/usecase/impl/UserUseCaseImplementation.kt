package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.mapper.UserMapper
import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import com.noom.interview.fullstack.sleep.infrastructure.exception.NotFoundException
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserUseCaseImplementation(
    @Autowired val userRepository: UserRepository,
    @Autowired val userMapper: UserMapper
) : UserUseCase {

    override fun getUser(idUser: String): ApiResponse<UserResponse?, Meta> {
        val user = getUserById(idUser)
        if (user == null) {
            throw NotFoundException()
        } else {
            val data: UserResponse = userMapper.toResponseFromUser(user)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User found with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        }
    }

    override fun createUser(userRequest: UserRequest): ApiResponse<UserResponse?, Meta> {
        validateName(userRequest.userName)
        var user: User = userMapper.toUserFromRequest(userRequest)
        user = userRepository.save(user)
        val data: UserResponse = userMapper.toResponseFromUser(user)
        return ApiResponse.Builder<UserResponse, Meta>().status("success").data(data)
            .message("User created with success!")
            .meta(Meta(1, 1, Instant.now().toString())).build()
    }

    override fun updateUser(userRequest: UserRequest, idUser: String): ApiResponse<UserResponse?, Meta> {
        validateName(userRequest.userName)
        val user = getUserById(idUser)
        if (user != null) {
            var userUpdated: User = userMapper.toUpdateUserFromRequest(userRequest, user)
            userUpdated = userRepository.save(userUpdated)
            val data: UserResponse = userMapper.toResponseFromUser(userUpdated)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User updated with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        } else throw NotFoundException()
    }

    override fun deleteUser(idUser: String): ApiResponse<UserResponse?, Meta> {
        val user = getUserById(idUser)
        if (user == null) {
            throw NotFoundException()
        } else {
            userRepository.delete(user)
            val data: UserResponse = userMapper.toResponseFromUser(user)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User deleted with success!")
                .meta(Meta(1, 1, Instant.now().toString())).build()
        }
    }

    override fun getUserById(idUser: String): User? =
        userRepository.findByIdUser(idUser)

    private fun validateName(userName: String) {
        if (userName.isBlank() || !userName.matches(Regex("^[A-Za-z ]+\$"))) {
            throw BadRequestException()
        }
    }
}