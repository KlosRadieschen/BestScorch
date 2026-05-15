package database.tables

import org.jetbrains.exposed.sql.Table

object Report : Table() {
    // Keys
    val type = integer("type")
    val authorRankIndicator = integer("authorRankIndicator")
    val id = integer("pk_ID")
    override val primaryKey = PrimaryKey(type, authorRankIndicator, id)
    val author = varchar("fk_characterID", 50)

    // Report
    val title = varchar("title", 255)
    val body = text("body")
}