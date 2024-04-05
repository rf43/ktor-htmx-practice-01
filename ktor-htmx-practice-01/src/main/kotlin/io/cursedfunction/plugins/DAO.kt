package io.cursedfunction.plugins

import io.cursedfunction.plugins.dao.CursedDAOPlugin
import io.ktor.server.application.*

fun Application.configureDAO() {
    install(CursedDAOPlugin) {
        connection {
            url = "jdbc:postgresql://localhost:5432/ktor-htmx"
            driver = "org.postgresql.Driver"
            user = "ktor-htmx"
        }
    }
}