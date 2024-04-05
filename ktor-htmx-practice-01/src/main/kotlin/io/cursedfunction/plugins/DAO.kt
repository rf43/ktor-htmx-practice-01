package io.cursedfunction.plugins

import io.cursedfunction.models.BlogPost
import io.cursedfunction.utils.datetime.timestampWithTimeZone
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureDAO() {
    install(CursedDAOPlugin) {
        connection {
            url = "jdbc:postgresql://localhost:5432/ktor-htmx"
            driver = "org.postgresql.Driver"
            user = "ktor-htmx"
        }
    }
}

interface CursedDAO {
    suspend fun getAllPosts(): List<BlogPost>
}

class CursedDAOImpl : CursedDAO {
    override suspend fun getAllPosts(): List<BlogPost> = CursedDAOConnection.dbQuery {
        CursedPostTable.selectAll().map { row ->
            BlogPost(
                id = row[CursedPostTable.id],
                title = row[CursedPostTable.title],
                content = row[CursedPostTable.content],
                timestamp = row[CursedPostTable.timestamp]
            )
        }
    }
}

private val CursedDAOPlugin: ApplicationPlugin<CursedDAOConfig> =
    createApplicationPlugin(name = "CursedDAOPlugin", ::CursedDAOConfig) {
        CursedDAOConnection.init(
            credentials = pluginConfig.credential ?: error("No config credentials found!")
        )
    }

private class CursedDAOConfig {
    var credential: CursedDBCredentials? = null

    inline fun connection(block: CursedDBCredentials.() -> Unit) {
        if (credential != null) return
        credential = CursedDBCredentials().apply(block)
    }
}

private class CursedDBCredentials {
    var url: String = ""
    var driver: String = ""
    var user: String = ""
    var password: String = ""
}

private object CursedDAOConnection {
    fun init(credentials: CursedDBCredentials) {
        Database.connect(
            url = credentials.url,
            driver = credentials.driver,
            user = credentials.user,
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

private object CursedPostTable : Table("cursed_posts") {
    val id = integer("post_id").autoIncrement()
    val title = varchar("post_title", 255)
    val content = text("post_content")
    val timestamp = timestampWithTimeZone("post_timestamp")

    override val primaryKey = PrimaryKey(id)
}