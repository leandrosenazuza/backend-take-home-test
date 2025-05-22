package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.model.User
import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.mapper.UserMapper
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.domain.usecase.UserUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserUseCaseImplementation(
    @Autowired val userRepository: UserRepository,
    @Autowired val userMapper: UserMapper
) : UserUseCase {
    override fun getUser(idUser: String): UserResponse? {
        val user = userRepository.findByIdUser(idUser)
        if (user != null) {
            return userMapper.toResponseFromUser(user)
        } else return null
        //TODO
        //else throw not found
    }

    override fun createUser(userRequest: UserRequest): UserResponse {
        var user: User = userMapper.toUserFromRequest(userRequest)
        user = userRepository.save(user)
        return userMapper.toResponseFromUser(user)
    }

    override fun updateUser(userRequest: UserRequest, userId: String): UserResponse {
        var user = userRepository.findByIdUser(userId)
        if (user != null) {
            var userUpdated: User = userMapper.toUpdateUserFromRequest(userRequest, user)

            userUpdated = userRepository.save(userUpdated)
            return userMapper.toResponseFromUser(userUpdated)
        }
        throw Exception()
    }

    override fun deleteUser(idUser: String) {
        val user = userRepository.findByIdUser(idUser)
        if (user != null) {
            userRepository.delete(user)
        }
        //TODO
        throw Exception()
    }
}