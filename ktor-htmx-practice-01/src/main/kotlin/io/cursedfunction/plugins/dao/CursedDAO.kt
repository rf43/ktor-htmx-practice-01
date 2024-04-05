package io.cursedfunction.plugins.dao

import io.cursedfunction.models.BlogPost
import io.cursedfunction.utils.datetime.timestampWithTimeZone
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface CursedDAO {
    suspend fun getAllPosts(): List<BlogPost>
}

class CursedDAOImpl : CursedDAO {
    override suspend fun getAllPosts(): List<BlogPost> = CursedDBConnection.dbQuery {
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

private object CursedDBConnection {
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