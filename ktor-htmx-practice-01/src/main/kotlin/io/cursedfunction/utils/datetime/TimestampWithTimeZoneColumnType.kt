package io.cursedfunction.utils.datetime

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.Table
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneOffset
import java.util.*

/**
 * A column type for a timestamp with timezone.
 *
 * **See:** https://github.com/PerfectDreams/ExposedPowerUtils/tree/main/postgres-java-time
 */

private class TimestampWithTimeZoneColumnType : ColumnType(), IDateColumnType {
    override val hasTimePart: Boolean = true

    private val utcZoneOffset = ZoneOffset.UTC
    private val calendarTimeZoneInstance = Calendar.getInstance(TimeZone.getTimeZone(utcZoneOffset))

    override fun nonNullValueToString(value: Any): String =
        when (value) {
            is java.time.Instant -> Timestamp.from(value).toString()
            is Instant -> "'$value'"
            else -> error("$value is not a java.time.Instant nor a kotlinx.datetime.Instant!")
        }

    override fun valueFromDB(value: Any): Any =
        when (value) {
            is Timestamp -> value.toInstant().toKotlinInstant()
            is java.time.Instant -> value.toKotlinInstant()
            is Instant -> value
            else -> error("$value is not a java.sql.Timestamp nor a java.time.Instant nor kotlinx.datetime.Instant!")
        }

    override fun readObject(rs: ResultSet, index: Int): Any? {
        return rs.getTimestamp(index, calendarTimeZoneInstance)
    }

    override fun sqlType() = "TIMESTAMP WITH TIME ZONE"

    override fun notNullValueToDB(value: Any): Any =
        when (value) {
            is java.time.Instant -> Timestamp.from(value)
            is Instant -> Timestamp.from(value.toJavaInstant())
            else -> error("$value is not a java.time.Instant nor a kotlinx.datetime.Instant!")
        }
}

/**
 * A timestamp with timezone to store an instant.
 *
 * **See:** https://www.toolbox.com/tech/data-management/blogs/zone-of-misunderstanding-092811/
 *
 * @param name The column name
 */
fun Table.timestampWithTimeZone(name: String): Column<Instant> =
    registerColumn(name, TimestampWithTimeZoneColumnType())