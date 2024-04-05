package io.cursedfunction.plugins

import io.cursedfunction.plugins.dao.CursedDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun Application.configureRouting(dao: CursedDAO) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            val posts = dao.getAllPosts()

            call.respondText(
                text = json.encodeToString(posts),
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK
            )
        }
    }
}
