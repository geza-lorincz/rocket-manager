package com.route

import com.models.MessageWrapper
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
    }
}
