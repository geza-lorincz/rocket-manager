package com.core

import kotlin.test.*
import com.models.MissionChangedContent
import com.models.RocketExplodedContent
import com.models.RocketLaunchContent
import core.RocketService

class RocketServiceTest {

    @BeforeTest
    fun setup() {
        // Clear internal state before each test
        RocketService.clear()
    }

    @Test
    fun `test rocket launch updates state`() {
        val channel = "test-channel"
        val content = RocketLaunchContent("Falcon-9", 1000, "TEST_MISSION")

        RocketService.processRocketLaunched(channel, 1, content)
        val state = RocketService.getRocketState(channel)

        assertNotNull(state)
        assertEquals("Falcon-9", state.type)
        assertEquals(1000, state.speed)
        assertEquals("TEST_MISSION", state.mission)
        assertFalse(state.exploded)
    }

    @Test
    fun `test speed increase updates correctly`() {
        val channel = "test-channel"
        RocketService.processRocketLaunched(channel, 1, RocketLaunchContent("Falcon-9", 1000, "MISSION"))

        RocketService.processSpeedChanged(channel, 2, 500, increase = true)
        val state = RocketService.getRocketState(channel)

        assertEquals(1500, state?.speed)
    }

    @Test
    fun `test speed decrease updates correctly`() {
        val channel = "test-channel"
        RocketService.processRocketLaunched(channel, 1, RocketLaunchContent("Falcon-9", 1000, "MISSION"))

        RocketService.processSpeedChanged(channel, 2, 300, increase = false)
        val state = RocketService.getRocketState(channel)

        assertEquals(700, state?.speed)
    }

    @Test
    fun `test rocket explosion updates exploded flag`() {
        val channel = "test-channel"
        RocketService.processRocketLaunched(channel, 1, RocketLaunchContent("Falcon-9", 1000, "MISSION"))

        RocketService.processRocketExploded(channel, 2, RocketExplodedContent("TEST_FAILURE"))
        val state = RocketService.getRocketState(channel)

        assertTrue(state?.exploded ?: false)
        assertEquals("TEST_FAILURE", state?.explosionReason)
    }

    @Test
    fun `test mission changed updates multiple rockets`() {
        val c1 = "channel-1"
        val c2 = "channel-2"
        RocketService.processRocketLaunched(c1, 1, RocketLaunchContent("Falcon-9", 1000, "OLD"))
        RocketService.processRocketLaunched(c2, 1, RocketLaunchContent("Atlas-V", 2000, "OLD"))

        RocketService.processMissionChanged(2, MissionChangedContent("Control-Center", listOf(c1, c2), "NEW_MISSION"))

        assertEquals("NEW_MISSION", RocketService.getRocketState(c1)?.mission)
        assertEquals("NEW_MISSION", RocketService.getRocketState(c2)?.mission)
    }

    @Test
    fun `test out-of-order message is ignored`() {
        val channel = "test-channel"
        RocketService.processRocketLaunched(channel, 5, RocketLaunchContent("Falcon-9", 1000, "MISSION"))

        RocketService.processSpeedChanged(channel, 3, 500, increase = true) // should be ignored
        val state = RocketService.getRocketState(channel)

        assertEquals(1000, state?.speed) // unchanged
    }

    @Test
    fun `test duplicate message is ignored`() {
        val channel = "test-channel"
        val content = RocketLaunchContent("Falcon-9", 1000, "MISSION")

        RocketService.processRocketLaunched(channel, 1, content)
        RocketService.processRocketLaunched(channel, 1, content) // duplicate

        val state = RocketService.getRocketState(channel)
        assertEquals(1000, state?.speed)
    }
}
