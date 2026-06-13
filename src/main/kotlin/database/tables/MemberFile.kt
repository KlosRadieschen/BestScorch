package database.tables

import org.jetbrains.exposed.v1.core.Table

object MemberFile : Table() {
	// Key
	val ownerID = integer("pkfk_characterID").references(CharacterTable.id)
	override val primaryKey = PrimaryKey(ownerID)

	val messageID = long("messageID")
}