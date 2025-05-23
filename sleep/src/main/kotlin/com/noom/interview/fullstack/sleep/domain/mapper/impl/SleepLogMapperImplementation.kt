package com.noom.interview.fullstack.sleep.domain.mapper.impl

import com.noom.interview.fullstack.sleep.domain.json.request.SleepLogRequest
import com.noom.interview.fullstack.sleep.domain.json.response.SleepLogResponse
import com.noom.interview.fullstack.sleep.domain.mapper.SleepLogMapper
import com.noom.interview.fullstack.sleep.domain.model.SleepLog

class SleepLogMapperImplementation() : SleepLogMapper {
    override fun toSleepLogFromRequest(sleepLogRequest: SleepLogRequest): SleepLog {
        TODO("Not yet implemented")
    }

    override fun toUpdateSleepLogFromRequest(sleepLogRequest: SleepLogRequest, sleepLog: SleepLog): SleepLog {
        TODO("Not yet implemented")
    }

    override fun toResponseFromSleepLog(sleepLog: SleepLog): SleepLogResponse {
        TODO("Not yet implemented")
    }
}