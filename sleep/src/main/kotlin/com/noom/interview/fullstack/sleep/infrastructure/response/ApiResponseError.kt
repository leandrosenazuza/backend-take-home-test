package com.noom.interview.fullstack.sleep.infrastructure.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponseError<T, E>(
    val errors: T?,
    val meta: E?,
) {
    class Builder<T : List<DataError>, E : Meta> {
        private var errors: T? = null
        private var meta: E = Meta() as E

        fun errors(errors: T) = apply { this.errors = errors }
        fun meta(meta: E) = apply { this.meta = meta }
        fun build() = ApiResponseError(errors, meta)
    }
}
