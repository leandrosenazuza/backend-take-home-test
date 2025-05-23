package com.noom.interview.fullstack.sleep.infrastructure.util

import ErrorEnum

fun getErrorEnumByException(exception: Exception): ErrorEnum =
    ErrorEnum.entries.find { it.error == exception::class } ?: ErrorEnum.INTERNAL_SERVER_ERROR_EXCEPTION