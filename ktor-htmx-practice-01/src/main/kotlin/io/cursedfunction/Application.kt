package io.cursedfunction

import io.cursedfunction.plugins.configureHTTP
import io.cursedfunction.plugins.configureRouting
import io.cursedfunction.plugins.configureSerialization
import io.cursedfunction.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureTemplating()
    configureRouting()
}
