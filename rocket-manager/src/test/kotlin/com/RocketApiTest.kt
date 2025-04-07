package com

import com.models.response.RocketResponse
import io.ktor.server.testing.*
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import com.route.configureRouting

class RocketApiTest {


    private fun Application.testModule() {
        configureRouting()
    }

    @Test
    fun `GET empty list of rockets`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/rockets")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"rockets":[]}""",
            response.bodyAsText()
        )
    }

    @Test
    fun `GET rocket after receiving a launch message`() = testApplication {
        application {
            testModule()
        }

        val launchMessage = """
            {
              "metadata": {
                "channel": "abc123",
                "messageNumber": 1,
                "messageTime": "2024-04-06T19:00:00+01:00",
                "messageType": "RocketLaunched"
              },
              "message": {
                "type": "Falcon-9",
                "launchSpeed": 500,
                "mission": "ARTEMIS"
              }
            }
        """.trimIndent()

        client.post("/messages") {
            contentType(ContentType.Application.Json)
            setBody(launchMessage)
        }

        val response = client.get("/rocket") {
            parameter("id", "abc123")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json { ignoreUnknownKeys = true }
        val body = json.decodeFromString(
            RocketResponse.serializer(),
            response.bodyAsText()
        )

        assertEquals("abc123", body.id)
        assertEquals("Falcon-9", body.type)
        assertEquals(500, body.speed)
        assertEquals("ARTEMIS", body.mission)
        assertEquals(false, body.exploded)
    }

    @Test
    fun `GET specific rocket after receiving multiple launch messages`() = testApplication {
        application {
            testModule()
        }

        val launchMessage = """
            {
              "metadata": {
                "channel": "abc123",
                "messageNumber": 1,
                "messageTime": "2024-04-06T19:00:00+01:00",
                "messageType": "RocketLaunched"
              },
              "message": {
                "type": "Falcon-9",
                "launchSpeed": 500,
                "mission": "ARTEMIS"
              }
            }
        """.trimIndent()

        val otherLaunchMessage = """
            {
              "metadata": {
                "channel": "abcxds",
                "messageNumber": 1,
                "messageTime": "2024-04-06T19:00:00+01:00",
                "messageType": "RocketLaunched"
              },
              "message": {
                "type": "Falcon-9",
                "launchSpeed": 500,
                "mission": "MOON"
              }
            }
        """.trimIndent()

        client.post("/messages") {
            contentType(ContentType.Application.Json)
            setBody(launchMessage)
        }
        client.post("/messages") {
            contentType(ContentType.Application.Json)
            setBody(otherLaunchMessage)
        }

        val response = client.get("/rocket") {
            parameter("id", "abc123")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json { ignoreUnknownKeys = true }
        val body = json.decodeFromString(
            RocketResponse.serializer(),
            response.bodyAsText()
        )

        assertEquals("abc123", body.id)
        assertEquals("Falcon-9", body.type)
        assertEquals(500, body.speed)
        assertEquals("ARTEMIS", body.mission)
        assertEquals(false, body.exploded)
    }
}
