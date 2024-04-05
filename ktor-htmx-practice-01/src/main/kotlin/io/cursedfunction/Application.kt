package io.cursedfunction

import io.cursedfunction.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = 80,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureDAO()
    configureHTTP()
    configureSerialization()
    configureTemplating()
    configureRouting(
        dao = CursedDAOImpl()
    )
}
