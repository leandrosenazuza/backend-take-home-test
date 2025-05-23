package com.noom.interview.fullstack.sleep.domain.usecase.impl

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.mapper.SleepLogMapper
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.usecase.SleepLogUseCase
import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponse
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import org.springframework.beans.factory.annotation.Autowired

class SleepLogUseCaseImplementation(
    @Autowired val sleepLogRepository: SleepLogRepository,
    @Autowired val sleepLogMapper: SleepLogMapper
) : SleepLogUseCase {
    override fun getSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }

    override fun createSleepLog(userRequest: SleepLogRequest): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }

    override fun updateSleepLog(userRequest: SleepLogRequest, userId: String): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }

    override fun deleteSleepLog(idSleep: String): ApiResponse<SleepLogResponse?, Meta> {
        TODO("Not yet implemented")
    }
}