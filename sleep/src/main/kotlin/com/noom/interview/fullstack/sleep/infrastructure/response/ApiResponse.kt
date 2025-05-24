package com.noom.interview.fullstack.sleep.infrastructure.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T, E>(
    var status: String = EnumSuccess.SUCCESS.value,
    var message: String = "",
    var data: T? = null,
    var meta: E? = null,
    var dataList: List<T>? = null
) {
    class Builder<T, E> {
        private var status: String = EnumSuccess.SUCCESS.value
        private var message: String = ""
        private var data: T? = null
        private var dataList: List<T?> = emptyList()
        private var meta: E? = null

        fun status(status: String) = apply { this.status = status }
        fun message(message: String) = apply { this.message = message }
        fun data(data: T) = apply { this.data = data }
        fun dataList(dataList: List<T>) = apply { this.dataList = dataList }
        fun meta(meta: E) = apply { this.meta = meta }

        fun build(): ApiResponse<T?, E> {
            return ApiResponse(status, message, data, meta, dataList)
        }
    }
}
