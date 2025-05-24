package com.noom.interview.fullstack.sleep.domain.repository

import com.noom.interview.fullstack.sleep.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository: JpaRepository <User, String>{
    @Query("select u.* from tab_user u where u.id_user  = :idUser", nativeQuery = true)
    fun findByIdUser(@Param("idUser") idUser: String) : User?
}