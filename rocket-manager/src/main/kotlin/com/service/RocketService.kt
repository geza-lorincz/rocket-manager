package com.service

import com.models.*
import org.slf4j.LoggerFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.concurrent.ConcurrentHashMap

object RocketService {

    private val rockets = ConcurrentHashMap<String, Rocket>()
    private val json = Json
    private val logger = LoggerFactory.getLogger(RocketService::class.java)


    fun handleMessage(wrapper: MessageWrapper) {
        val channel = wrapper.metadata.channel
        val messageNumber = wrapper.metadata.messageNumber
        val type = wrapper.metadata.messageType

        val rocket = rockets.computeIfAbsent(channel) { Rocket(channel = channel) }

        // Skip if we've already processed a newer or same message
        if (messageNumber <= rocket.lastMessageNumber) throw IllegalStateException("Message has already been processed before or is stale")

        logger.info("Handling $type for channel=$channel with content=${wrapper.message}")

        when (type) {
            "RocketLaunched" -> {
                val payload = json.decodeFromJsonElement<RocketLaunched>(wrapper.message)
                rocket.type = payload.type
                rocket.speed = payload.launchSpeed
                rocket.mission = payload.mission
            }
            "RocketSpeedIncreased" -> {
                val payload = json.decodeFromJsonElement<RocketSpeedIncreased>(wrapper.message)
                rocket.speed += payload.by
            }
            "RocketSpeedDecreased" -> {
                val payload = json.decodeFromJsonElement<RocketSpeedDecreased>(wrapper.message)
                rocket.speed -= payload.by
            }
            "RocketExploded" -> {
                val payload = json.decodeFromJsonElement<RocketExploded>(wrapper.message)
                rocket.exploded = true
                rocket.explosionReason = payload.reason
            }
            "MissionChanged" -> {
                val payload = json.decodeFromJsonElement<MissionChanged>(wrapper.message)
                if (channel in payload.channels) {
                    rocket.mission = payload.newMission
                }
            }
        }

        rocket.lastMessageNumber = messageNumber
    }

    fun getRocket(id: String): Rocket? = rockets[id]

    fun getAllRockets(): List<Rocket> = rockets.values.sortedBy { it.channel }

    fun clearAllRockets() = rockets.clear()
}
