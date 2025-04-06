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
data class MessageWrapper(
    val metadata: MessageMetadata,
    val message: kotlinx.serialization.json.JsonElement
)
