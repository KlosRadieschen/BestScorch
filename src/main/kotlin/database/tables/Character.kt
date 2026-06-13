package database.tables

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object CharacterTable : IntIdTable() {
    // Meta
    val ownerID = ulong("ownerID")
    val sanitizedName = varchar("sanitizedName", 50)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    // Person
    val name =  varchar("name", 50)
    val age = varchar("age", 25).nullable()
    val gender = varchar("gender", 50).nullable()
    val height = varchar("height", 50).nullable()
    val appearance = text("appearance").nullable()
    val image = varchar("image", 2083).nullable()

    // Military
    val rank = varchar("rank", 50).nullable()
    val battalion = varchar("battalion", 50).nullable()

    // Lore
    val lore = text("lore").nullable()
    val traits = text("traits").nullable()
    val likes = varchar("likes", 255).nullable()
    val dislikes = varchar("dislikes", 255).nullable()

    // Misc
    val misc = text("misc").nullable()
}

class CharacterEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CharacterEntity>(CharacterTable)

    // Meta
    var ownerID by CharacterTable.ownerID
    var sanitizedName by CharacterTable.sanitizedName
    var createdAt by CharacterTable.createdAt

    // Person
    var name by CharacterTable.name
    var age by CharacterTable.age
    var gender by CharacterTable.gender
    var height by CharacterTable.height
    var appearance by CharacterTable.appearance
    var image by CharacterTable.image

    // Military
    var rank by CharacterTable.rank
    var battalion by CharacterTable.battalion

    // Lore
    var lore by CharacterTable.lore
    var traits by CharacterTable.traits
    var likes by CharacterTable.likes
    var dislikes by CharacterTable.dislikes

    // Misc
    var misc by CharacterTable.misc

    fun mappedShortFields() : Map<String, String?> {
        return mapOf(
            "Age" to age,
            "Gender" to gender,
            "Height" to height,
            "Battalion" to battalion,
            "Rank" to rank,
        )
    }
}