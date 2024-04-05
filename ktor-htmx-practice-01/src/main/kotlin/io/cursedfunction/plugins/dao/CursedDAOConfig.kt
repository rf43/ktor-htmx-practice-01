package io.cursedfunction.plugins.dao

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

internal class CursedDAOConfig {
    var credential: CursedDBCredentials? = null

    inline fun connection(block: CursedDBCredentials.() -> Unit) {
        if (credential != null) return
        credential = CursedDBCredentials().apply(block)
        credential?.let {
            require(it.url.isNotBlank()) { "Database URL cannot be blank" }
            require(it.driver.isNotBlank()) { "Database driver cannot be blank" }
            require(it.user.isNotBlank()) { "Database user cannot be blank" }
        }
    }
}

internal class CursedDBCredentials {
    var url: String = ""
    var driver: String = ""
    var user: String = ""
    var password: String = ""
}

internal val CursedDAOPlugin: ApplicationPlugin<CursedDAOConfig> =
    createApplicationPlugin(name = "CursedDAOPlugin", ::CursedDAOConfig) {
        val credentials = pluginConfig.credential ?: error("No config credentials found!")
        Database.connect(
            url = credentials.url,
            driver = credentials.driver,
            user = credentials.user,
            password = credentials.password
        )
    }