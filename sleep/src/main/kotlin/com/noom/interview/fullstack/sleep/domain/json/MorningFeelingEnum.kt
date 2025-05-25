package com.noom.interview.fullstack.sleep.domain.json

enum class MorningFeelingEnum(val displayName: String) {
    BAD("Bad"),
    OK("OK"),
    GOOD("Good");

    companion object {
        fun fromString(value: String): MorningFeelingEnum? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}