package com.noom.interview.fullstack.sleep.domain.constants

/*
* USER URIs
* */

// User: (/api/v1/user)
const val URI_USER_V1 = "/api/v1/user"

// User: (GET /api/v1/user/{idUser})
const val URI_GET_USER_V1 = "$URI_USER_V1/{idUser}"

// User: (PUT /api/v1/user/{idUser})
const val URI_PUT_USER_V1 = "$URI_USER_V1/{idUser}"

// User: (POST /api/v1/user/{idUser})
const val URI_POST_USER_V1 = URI_USER_V1

// User: (DELETE /api/v1/user/{idUser})
const val URI_DELETE_USER_V1 = "$URI_USER_V1/{idUser}"

/*
* SLEEP URIs
* */

// SleepLog: (/api/v1/user)
const val URI_SLEEP_LOG_V1 = "/api/v1/sleep"

// SleepLog: (GET /api/v1/sleep/{idSleep})
const val URI_GET_SLEEP_BY_ID_SLEEP_LOG_V1 = "$URI_SLEEP_LOG_V1/{idSleep}"

// SleepLog: (GET /api/v1/sleep/{idUser})
const val URI_GET_LAST_NIGHT_SLEEP_BY_ID_USER_V1 = "$URI_SLEEP_LOG_V1/{idUser}/last-night"

// SleepLog: (GET /api/v1/sleep/{idUser})
const val URI_GET_LAST_THIRTY_DAYS_SLEEP_BY_ID_USER_V1 = "$URI_SLEEP_LOG_V1/{idUser}/last-thirty-days"

// SleepLog: (PUT /api/v1/user/{idUser})
const val URI_PUT_SLEEP_LOG_V1 = "$URI_SLEEP_LOG_V1/{idSleep}"

// SleepLog: (POST /api/v1/user/{idUser})
const val URI_POST_SLEEP_LOG_V1 = URI_SLEEP_LOG_V1

// SleepLog: (DELETE /api/v1/user/{idUser})
const val URI_DELETE_SLEEP_LOG_V1 = "$URI_SLEEP_LOG_V1/{idSleep}"


