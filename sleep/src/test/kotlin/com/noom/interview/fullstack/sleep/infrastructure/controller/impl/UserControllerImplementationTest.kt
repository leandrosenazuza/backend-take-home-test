package com.noom.interview.fullstack.sleep.infrastructure.controller.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.domain.constants.*
import com.noom.interview.fullstack.sleep.domain.json.response.UserResponse
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.helper.model.createSleepLogEntityMock
import com.noom.interview.fullstack.sleep.helper.model.createUserEntityMock
import com.noom.interview.fullstack.sleep.helper.request.createUserRequestMock
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import com.noom.interview.fullstack.sleep.infrastructure.util.getDifferenceOfTime
import com.noom.interview.fullstack.sleep.infrastructure.util.getZoneId
import com.noom.interview.fullstack.sleep.infrastructure.util.localDateTimeToInstant
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.util.*

@Tag("UserIntegrationTest")
class UserControllerImplementationTest : AbstractTest() {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should successfully get user by idUser`() {
        val idUser = UUID.randomUUID().toString()
        val user = createUserEntityMock(idUser = idUser)

        userRepository.save(user)

        mockMvc.perform(
            MockMvcRequestBuilders.get(URI_GET_USER_V1.replace("{idUser}", idUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(user.username))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should successfully create user`() {
        val userName = "testUser"
        val userRequest = createUserRequestMock(userName)

        val mock = mockMvc.perform(
            MockMvcRequestBuilders.post(URI_POST_USER_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(userRequest))
        ).andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())

        val allUsers = userRepository.findAll()
        assertEquals(1, allUsers.size)
        val userSaved = allUsers[0]

        mock
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(userSaved.idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(userSaved.username))
    }

    @Test
    fun `Should successfully update user`() {
        val oldUser = createUserEntityMock(username = "Old Username")
        userRepository.save(oldUser)

        val newUserName = "New Username"
        val updateRequest = createUserRequestMock(newUserName)

        mockMvc.perform(
            MockMvcRequestBuilders
                .put(URI_PUT_USER_V1.replace("{idUser}", oldUser.idUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(updateRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.idUser").value(oldUser.idUser))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(newUserName))
            .andDo(MockMvcResultHandlers.print())

        val updatedUser = userRepository.findById(oldUser.idUser).orElseThrow()
        assertEquals(newUserName, updatedUser.username)
    }

    @Test
    fun `Should successfully delete user by id`() {
        val userToDelete = createUserEntityMock()
        userRepository.save(userToDelete)

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(URI_DELETE_USER_V1.replace("{idUser}", userToDelete.idUser))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())

        val allUsers = userRepository.findAll()
        assertTrue(allUsers.isEmpty(), "User should be deleted from the database")
    }

    @Test
    fun `Should return BAD_REQUEST if userName is invalid when create the user`() {
        val invalidUserName = "1234_#@"
        val userRequest = createUserRequestMock(invalidUserName)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URI_POST_USER_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(userRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun `Should return BAD_REQUEST if userName is invalid when update the user`() {
        val oldUser = createUserEntityMock(username = "Old Username")
        userRepository.save(oldUser)

        val newUserName = "1234_#@"
        val updateRequest = createUserRequestMock(newUserName)

        mockMvc.perform(
            MockMvcRequestBuilders
                .put(URI_PUT_USER_V1.replace("{idUser}", oldUser.idUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(updateRequest))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }
}