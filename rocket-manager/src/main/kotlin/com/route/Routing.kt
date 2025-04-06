package com.route

import com.models.MessageWrapper
import com.models.Rocket
import com.models.toResposne
import com.service.RocketService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/messages") {
            val body = call.receiveText()
            val message = Json.decodeFromString<MessageWrapper>(body)
            RocketService.handleMessage(message)
            call.respondText("ok")
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
            else call.respondText("Rocket not found", status = io.ktor.http.HttpStatusCode.NotFound)
        }
    }
}
