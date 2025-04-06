package com.models

data class RocketState(
    val channel: String,
    var type: String? = null,
    var speed: Int = 0,
    var mission: String? = null,
    var exploded: Boolean = false,
    var explosionReason: String? = null,
    var lastMessageNumber: Long = 0
)

