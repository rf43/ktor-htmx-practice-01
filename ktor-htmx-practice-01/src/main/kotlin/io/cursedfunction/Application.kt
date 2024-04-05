package io.cursedfunction

import io.cursedfunction.plugins.configureHTTP
import io.cursedfunction.plugins.configureRouting
import io.cursedfunction.plugins.configureSerialization
import io.cursedfunction.plugins.configureTemplating
import io.cursedfunction.plugins.dao.CursedDAOImpl
import io.cursedfunction.plugins.dao.configureDAO
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
