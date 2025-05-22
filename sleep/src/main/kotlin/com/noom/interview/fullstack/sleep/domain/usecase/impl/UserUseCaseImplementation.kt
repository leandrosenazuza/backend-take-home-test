package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.mapper.UserMapper
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
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
        val user = userRepository.findByIdUser(idUser)
        if (user != null) {
            val data: UserResponse = userMapper.toResponseFromUser(user)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User found with success!").meta(Meta(1, 1, Instant.now().toString())).build()
        }
        throw Exception()
    }

    override fun createUser(userRequest: UserRequest): ApiResponse<UserResponse?, Meta> {
        var user: User = userMapper.toUserFromRequest(userRequest)
        user = userRepository.save(user)
        val data: UserResponse = userMapper.toResponseFromUser(user)
        return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User created with success!").meta(Meta(1, 1, Instant.now().toString())).build()
    }

    override fun updateUser(userRequest: UserRequest, userId: String): ApiResponse<UserResponse?, Meta> {
        val user = userRepository.findByIdUser(userId)
        if (user != null) {
            var userUpdated: User = userMapper.toUpdateUserFromRequest(userRequest, user)
            userUpdated = userRepository.save(userUpdated)
            val data: UserResponse = userMapper.toResponseFromUser(userUpdated)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User updated with success!").meta(Meta(1, 1, Instant.now().toString())).build()
        }
        throw Exception()
    }

    override fun deleteUser(idUser: String): ApiResponse<UserResponse?, Meta> {
        val user = userRepository.findByIdUser(idUser)
        if (user != null) {
            userRepository.delete(user)
            val data: UserResponse = userMapper.toResponseFromUser(user)
            return ApiResponse.Builder<UserResponse, Meta>().data(data).message("User deleted with success!").meta(Meta(1, 1, Instant.now().toString())).build()
        }
        throw Exception()
    }
}