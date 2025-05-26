package com.noom.interview.fullstack.sleep.infrastructure.handler

import com.noom.interview.fullstack.sleep.infrastructure.response.ApiResponseError
import com.noom.interview.fullstack.sleep.infrastructure.response.DataError
import com.noom.interview.fullstack.sleep.infrastructure.response.Meta
import com.noom.interview.fullstack.sleep.infrastructure.util.getErrorEnumByException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponseError<List<DataError>, Meta>> {
        val errorEnum = getErrorEnumByException(ex)
        val errors = listOf(
            DataError(
                title = errorEnum.title,
                detail = errorEnum.detail
            )
        )
        val meta = Meta(errors.size, 1)
        val response = ApiResponseError.Builder<List<DataError>, Meta>()
            .errors(errors)
            .meta(meta)
            .build()

        logger.error("Original Error on API: {}", ex.message)
        return ResponseEntity(response, HttpStatus.valueOf(errorEnum.statusCode))
    }

}