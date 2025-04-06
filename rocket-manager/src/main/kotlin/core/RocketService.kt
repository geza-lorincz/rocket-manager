package core

import com.models.MissionChangedContent
import com.models.RocketExplodedContent
import com.models.RocketLaunchContent
import com.models.RocketState
import java.util.concurrent.ConcurrentHashMap

object RocketService {
    private val rockets = ConcurrentHashMap<String, RocketState>()

    fun processRocketLaunched(channel: String, messageNumber: Long, content: RocketLaunchContent) {
        val state = rockets.compute(channel) { _, existing ->
            if (existing == null || messageNumber > existing.lastMessageNumber) {
                RocketState(
                    channel = channel,
                    type = content.type,
                    speed = content.launchSpeed,
                    mission = content.mission,
                    lastMessageNumber = messageNumber
                )
            } else {
                existing
            }
        }
    }

    fun processSpeedChanged(channel: String, messageNumber: Long, by: Int, increase: Boolean) {
        rockets.computeIfPresent(channel) { _, state ->
            if (messageNumber > state.lastMessageNumber) {
                state.speed = if (increase) state.speed + by else state.speed - by
                state.lastMessageNumber = messageNumber
            }
            state
        }
    }

    // Add similar methods for RocketExploded and MissionChanged
    // Handle MissionChanged by updating a set of rockets given by channels.
    fun processRocketExploded(channel: String, messageNumber: Long, content: RocketExplodedContent) {
        rockets.computeIfPresent(channel) { _, state ->
            if (messageNumber > state.lastMessageNumber) {
                state.exploded = true
                state.explosionReason = content.reason
                state.lastMessageNumber = messageNumber
            }
            state
        }
    }

    fun processMissionChanged(messageNumber: Long, content: MissionChangedContent) {
        content.channels.forEach { channel ->
            rockets.computeIfPresent(channel) { _, state ->
                if (messageNumber > state.lastMessageNumber) {
                    state.mission = content.newMission
                    state.lastMessageNumber = messageNumber
                }
                state
            }
        }
    }

    // Accessor methods for the REST API
    fun getRocketState(channel: String): RocketState? = rockets[channel]
    fun getAllRockets(): List<RocketState> = rockets.values.toList()
    fun clear() = rockets.clear()
}
