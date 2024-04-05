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
    install(CursedDAOPlugin)
}

interface CursedDAO {
    suspend fun getAllPosts(): List<BlogPost>
}

class CursedDAOImpl : CursedDAO {
    override suspend fun getAllPosts(): List<BlogPost> = DAOConnection.dbQuery {
        PostTable.selectAll().map { row ->
            BlogPost(
                id = row[PostTable.id],
                title = row[PostTable.title],
                content = row[PostTable.content],
                timestamp = row[PostTable.timestamp]
            )
        }
    }
}

private val CursedDAOPlugin: ApplicationPlugin<DAOPluginConfig> =
    createApplicationPlugin(name = "DAOPlugin", ::DAOPluginConfig) { DAOConnection.init() }

private class DAOPluginConfig

private object DAOConnection {
    fun init() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/ktor-htmx",
            driver = "org.postgresql.Driver",
            user = "ktor-htmx",
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

private object PostTable : Table("cursed_posts") {
    val id = integer("post_id").autoIncrement()
    val title = varchar("post_title", 255)
    val content = text("post_content")
    val timestamp = timestampWithTimeZone("post_timestamp")

    override val primaryKey = PrimaryKey(id)
}