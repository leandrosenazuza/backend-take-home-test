package com.noom.interview.fullstack.sleep.domain.repository

import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SleepLogRepository : JpaRepository<SleepLog, String> {
    @Query("select tsl.* from tab_sleep_log tsl where tsl.id_sleep  = :idSleep", nativeQuery = true)
    fun findByIdUser(@Param("idSleep") userId: String) : SleepLog?
}