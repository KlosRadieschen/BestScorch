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
    val name = varchar("name", 50)
    val description = text("description")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    // Appearance
    val design = text("design")

    // Combat
    val primaryWeapon = text("primaryWeapon")
    val ordnance = text("ordnance")
    val defensiveAbility = text("defensiveAbility")
    val pointDefense = text("pointDefense")
    val core = text("core")

    // Misc
    val misc = text("misc")
}