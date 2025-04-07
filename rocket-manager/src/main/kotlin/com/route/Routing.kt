package com.route

import com.models.MessageWrapper
import com.models.toResposne
import com.service.RocketService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respondText("Ok")
        }
        val logger = LoggerFactory.getLogger("Routes")


        post("/messages") {
            try {
                val body = call.receiveText()
                val message = Json.decodeFromString<MessageWrapper>(body)
                logger.info("Received message: ${message.metadata.messageType} for ${message.metadata.channel}")
                RocketService.handleMessage(message)
                call.respondText("Message received and processed successfully")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
                logger.error(e.message ?: "Invalid request")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "An unexpected error occurred")
                logger.error(e.message ?: "Invalid request")
            }
        }

        get("/rockets") {
            val rockets = RocketService.getAllRockets()
                .map { it.toResposne() }
            call.respond(mapOf("rockets" to rockets))
        }

        get("/rocket") {
            val id = call.request.queryParameters["id"]
            val rocket = id?.let { RocketService.getRocket(it) }
            if (rocket != null) call.respond(rocket.toResposne())
            else call.respondText("Rocket not found", status = HttpStatusCode.NotFound)
        }
    }
}
