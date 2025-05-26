package com.noom.interview.fullstack.sleep.helper.request

import com.noom.interview.fullstack.sleep.domain.json.request.UserRequest

fun createUserRequestMock(
    userName: String = "",
): UserRequest {
    return UserRequest(
        userName = userName
    )
}