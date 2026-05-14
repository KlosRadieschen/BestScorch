package database.tables

import org.jetbrains.exposed.sql.Table

object Character : Table() {
    // Key
    val id = integer("pk_ID").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    // Meta
    val ownerID = integer("ownerID")

    // Person
    val name =  varchar("name", 50)
    val age = integer("age").nullable()
    val gender = varchar("gender", 50).nullable()
    val height = integer("height").nullable()
    val appearance = text("appearance").nullable()
    val image = blob("image").nullable()

    // Military
    val rank = integer("rank").nullable()
    val battalion = integer("battalion").nullable()

    // Lore
    val lore = text("lore").nullable()
    val traits = text("traits").nullable()
    val likes = varchar("likes", 255).nullable()
    val dislikes = varchar("dislikes", 255).nullable()

    // Misc
    val misc = text("misc").nullable()
}