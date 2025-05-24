package com.noom.interview.fullstack.sleep.domain.json.request

import com.noom.interview.fullstack.sleep.infrastructure.util.NAME_PATTERN
import javax.validation.constraints.Pattern

data class UserRequest(
    @field:Pattern(regexp = NAME_PATTERN)
    val userName: String
)