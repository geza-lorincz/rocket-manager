package com.models

import com.models.response.RocketResponse

data class Rocket(
    val channel: String,
    var type: String = "",
    var speed: Int = 0,
    var mission: String = "",
    var exploded: Boolean = false,
    var explosionReason: String? = null,
    var lastMessageNumber: Int = 0
)

fun Rocket.toResposne() = RocketResponse(
    id = this.channel,
    type = this.type,
    speed = this.speed,
    mission = this.mission,
    exploded = this.exploded,
    explosionReason = this.explosionReason
)

