package database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.regexp
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Titan : Table() {
    // Keys
    val callsign = varchar("pk_callsign", 7).check { it regexp "^[A-Z]{2}-\\d{4}$" }
    override val primaryKey = PrimaryKey(callsign)
    val ownerID = integer("fk_characterID").references(CharacterTable.id)

    // General
    val name = varchar("name", 50).nullable()
    val titanClass = varchar("class", 50).nullable()
    val chassis = varchar("chassis", 50).nullable()
    val description = text("description").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    // Appearance
    val design = text("design").nullable()

    // Combat
    val primaryWeapon = text("primaryWeapon").nullable()
    val ordinance = text("ordinance").nullable()
    val defensive = text("defensive").nullable()
    val tactical = text("tactical").nullable()
    val kits = text("kits").nullable()
    val core = text("core").nullable()

    // Misc
    val misc = text("misc").nullable()
}