package com.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageMetadata(
    val channel: String,
    val messageNumber: Int,
    val messageTime: String,
    val messageType: String
)

@Serializable
data class RocketLaunchContent(
    val type: String,
    val launchSpeed: Int,
    val mission: String
)

@Serializable
data class RocketSpeedChangedContent(
    val by: Int
)

@Serializable
data class RocketExplodedContent(
    val reason: String
)

@Serializable
data class MissionChangedContent(
    val center: String,
    val channels: List<String>,
    val newMission: String
)

@Serializable
data class RocketMessage<T>(
    val metadata: MessageMetadata,
    val content: T
)
