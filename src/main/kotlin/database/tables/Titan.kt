package database.tables

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object TitanTable : IdTable<String>() {
    // Keys
    val callsign = varchar("pk_callsign", 7)
    override val id = callsign.entityId()
    val ownerID = ulong("ownerID")
    val assignedCharacterID = reference("fk_characterID", CharacterTable).nullable()

    // General
    val name = varchar("name", 50).nullable()
    val titanClass = varchar("class", 50).nullable()
    val chassis = varchar("chassis", 50).nullable()
    val kits = varchar("kits", 255).nullable()
    val description = text("description").nullable()
    val image = varchar("image", 2083).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    // Appearance
    val design = text("design").nullable()

    // Combat
    val primaryWeapon = varchar("primaryWeapon", 255).nullable()
    val ordinance = varchar("ordinance", 255).nullable()
    val defensive = varchar("defensive", 255).nullable()
    val tactical = varchar("tactical", 255).nullable()
    val core = varchar("core", 255).nullable()

    // Misc
    val misc = text("misc").nullable()
}

class TitanEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, TitanEntity>(TitanTable)

    // Keys
    var callsign by TitanTable.callsign
    var ownerID by TitanTable.ownerID
    var character by CharacterEntity optionalReferencedOn TitanTable.assignedCharacterID

    // General
    var name by TitanTable.name
    var titanClass by TitanTable.titanClass
    var chassis by TitanTable.chassis
    var kits by TitanTable.kits
    var description by TitanTable.description
    var image by TitanTable.image
    var createdAt by TitanTable.createdAt

    // Appearance
    var design by TitanTable.design

    // Combat
    var primaryWeapon by TitanTable.primaryWeapon
    var ordinance by TitanTable.ordinance
    var defensive by TitanTable.defensive
    var tactical by TitanTable.tactical
    var core by TitanTable.core

    // Misc
    var misc by TitanTable.misc

    fun mappedShortFields() : Map<String, String?> {
        return mapOf(
            "Class" to titanClass,
            "Chassis" to chassis,
            "Kits" to kits,
            "Design" to design,
            "Primary Weapon" to primaryWeapon,
            "Ordinance" to ordinance,
            "Defensive" to defensive,
            "Tactical" to tactical,
            "Core" to core,
        )
    }

    fun mappedLongFields() : Map<String, String?> {
        return mapOf(
            "Description" to description,
            "Misc" to misc,
        )
    }

    fun hasLongFields() : Boolean = mappedLongFields().isEmpty()
}