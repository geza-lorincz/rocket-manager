package com

import com.models.*
import com.service.RocketService
import kotlinx.serialization.json.*
import kotlin.test.*

class RocketServiceTest {

    private val service = RocketService

    @BeforeTest
    fun setup() {
        service.clearAllRockets()
    }

    @Test
    fun `should launch rocket and store correct data`() {
        val message = MessageWrapper(
            metadata = MessageMetadata("abc", 1, "2022-01-01T00:00:00Z", MessageType.RocketLaunched),
            message = Json.encodeToJsonElement(RocketLaunched("Falcon-9", 500, "ARTEMIS"))
        )

        service.handleMessage(message)

        val rocket = service.getRocket("abc")
        assertNotNull(rocket)
        assertEquals("Falcon-9", rocket.type)
        assertEquals(500, rocket.speed)
        assertEquals("ARTEMIS", rocket.mission)
        assertEquals(1, rocket.lastMessageNumber)
    }

    @Test
    fun `should increase rocket speed`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val speedUp = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.RocketSpeedIncreased),
            Json.encodeToJsonElement(RocketSpeedIncreased(400))
        )

        service.handleMessage(launch)
        service.handleMessage(speedUp)

        val rocket = service.getRocket("abc")!!
        assertEquals(500, rocket.speed)
    }

    @Test
    fun `should decrease rocket speed`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val slowDown = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.RocketSpeedDecreased),
            Json.encodeToJsonElement(RocketSpeedDecreased(50))
        )

        service.handleMessage(launch)
        service.handleMessage(slowDown)

        val rocket = service.getRocket("abc")!!
        assertEquals(50, rocket.speed)
    }

    @Test
    fun `should ignore out-of-order message`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 3, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val duplicate = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.RocketSpeedIncreased),
            Json.encodeToJsonElement(RocketSpeedIncreased(400))
        )

        service.handleMessage(launch)
        assertFailsWith<IllegalStateException> {
            service.handleMessage(duplicate)
        }

        val rocket = service.getRocket("abc")!!
        assertEquals(100, rocket.speed) // unchanged
    }

    @Test
    fun `should mark rocket as exploded and save explosion reason`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val explode = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.RocketExploded),
            Json.encodeToJsonElement(RocketExploded("ENGINE_FAILURE"))
        )

        service.handleMessage(launch)
        service.handleMessage(explode)

        val rocket = service.getRocket("abc")!!
        assertTrue(rocket.exploded)
        assertEquals("ENGINE_FAILURE", rocket.explosionReason)
    }

    @Test
    fun `should change mission only if channel is included`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val missionChange = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.MissionChanged),
            Json.encodeToJsonElement(
                MissionChanged(
                    center = "ESA",
                    channels = listOf("abc", "zzz"),
                    newMission = "JUPITER"
                )
            )
        )

        service.handleMessage(launch)
        service.handleMessage(missionChange)

        val rocket = service.getRocket("abc")!!
        assertEquals("JUPITER", rocket.mission)
    }

    @Test
    fun `should not change mission if channel not included`() {
        val launch = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val missionChange = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.MissionChanged),
            Json.encodeToJsonElement(
                MissionChanged(
                    center = "ESA",
                    channels = listOf("xyz", "zzz"),
                    newMission = "SATURN"
                )
            )
        )

        service.handleMessage(launch)
        service.handleMessage(missionChange)

        val rocket = service.getRocket("abc")!!
        assertEquals("MOON", rocket.mission)
    }

    @Test
    fun `should update mission for multiple channels`() {
        val launch1 = MessageWrapper(
            MessageMetadata("abc", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-9", 100, "MOON"))
        )
        val launch2 = MessageWrapper(
            MessageMetadata("xyz", 1, "now", MessageType.RocketLaunched),
            Json.encodeToJsonElement(RocketLaunched("Falcon-Heavy", 200, "MARS"))
        )
        val missionChange = MessageWrapper(
            MessageMetadata("abc", 2, "now", MessageType.MissionChanged),
            Json.encodeToJsonElement(
                MissionChanged(
                    center = "ESA",
                    channels = listOf("abc", "xyz"),
                    newMission = "JUPITER"
                )
            )
        )

        service.handleMessage(launch1)
        service.handleMessage(launch2)
        service.handleMessage(missionChange)

        val rocket1 = service.getRocket("abc")!!
        val rocket2 = service.getRocket("xyz")!!

        assertEquals("JUPITER", rocket1.mission)
        assertEquals("JUPITER", rocket2.mission)
    }
}
