package com.noom.interview.fullstack.sleep.infrastructure.util

import com.noom.interview.fullstack.sleep.AbstractTest
import com.noom.interview.fullstack.sleep.infrastructure.exception.BadRequestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnumUtilsKtTest : AbstractTest() {
    @Test
    fun badRequest_maps_to_enum() {
        assertEquals(
            ErrorEnum.BAD_REQUEST_EXCEPTION,
            getErrorEnumByException(BadRequestException())
        )
    }
}