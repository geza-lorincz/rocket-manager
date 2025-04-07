package com.models.response

import kotlinx.serialization.Serializable

@Serializable
data class RocketResponse(
    val id: String,
    val type: String,
    val speed: Int,
    val mission: String,
    val exploded: Boolean,
    val explosionReason: String? = null
)
