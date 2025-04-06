package com.models

import kotlinx.serialization.Serializable

@Serializable
data class RocketLaunched(
    val type: String,
    val launchSpeed: Int,
    val mission: String
)

@Serializable
data class RocketSpeedIncreased(val by: Int)

@Serializable
data class RocketSpeedDecreased(val by: Int)

@Serializable
data class RocketExploded(val reason: String)

@Serializable
data class MissionChanged(
    val center: String,
    val channels: List<String>,
    val newMission: String
)

