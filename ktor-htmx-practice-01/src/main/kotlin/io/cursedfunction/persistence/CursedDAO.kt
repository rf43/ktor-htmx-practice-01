package io.cursedfunction.persistence

import io.cursedfunction.models.BlogPost
import io.cursedfunction.plugins.dao.CursedDBConnection
import io.cursedfunction.utils.datetime.timestampWithTimeZone
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

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

private object CursedPostTable : Table("cursed_posts") {
    val id = integer("post_id").autoIncrement()
    val title = varchar("post_title", 255)
    val content = text("post_content")
    val timestamp = timestampWithTimeZone("post_timestamp")

    override val primaryKey = PrimaryKey(id)
}