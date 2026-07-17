package database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object StatsTable : IntIdTable() {
    // Foreign key to CharacterTable
    val character = reference("ownerID", CharacterTable)

    // Info
    val marksmanship = integer("marksmanship").boundsCheck(0, 22)
    val cqc = integer("cqc").boundsCheck(0, 22)
    val mobility = integer("mobility").boundsCheck(0, 22)
    val tactics = integer("tactics").boundsCheck(0, 22)
    val titanHandling = integer("titanHandling").boundsCheck(0, 22)

    private fun Column<Int>.boundsCheck(min: Int, max:Int): Column<Int> = this.check { it greaterEq min and (it lessEq max) }
}

class StatsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StatsEntity>(StatsTable) {
        fun numberToGrade(number: Int) = when {
            number <= 3 -> "D"
            number <= 6 -> "C"
            number <= 9 -> "B"
            number <= 12 -> "B+"
            number <= 15 -> "A"
            number <= 18 -> "A+"
            number <= 21 -> "S"
            else -> "S+"
        }
    }

    var character by CharacterEntity referencedOn StatsTable.character
    var marksmanship by StatsTable.marksmanship
    var cqc by StatsTable.cqc
    var mobility by StatsTable.mobility
    var tactics by StatsTable.tactics
    var titanHandling by StatsTable.titanHandling

    fun mappedFields() : Map<String, Int?> {
        return mapOf(
            "Marksmanship" to marksmanship,
            "CQC" to cqc,
            "Mobility" to mobility,
            "Tactics" to tactics,
            "Titan Handling" to titanHandling,
        )
    }
}