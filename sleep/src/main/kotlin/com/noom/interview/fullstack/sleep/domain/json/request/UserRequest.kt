package com.noom.interview.fullstack.sleep.domain.json.request

import javax.validation.constraints.Pattern

data class UserRequest(
    @field:Pattern(regexp = "^[a-zA-Z ]", message = "Only letters and spaces are allowed")
    val userName: String
)