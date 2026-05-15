package database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and

object CombatInfo : Table() {
    // Keys
    val ownerID = integer("pkfk_ownerID").references(Character.id, ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(ownerID)

    // Info
    val marksmanship = integer("marksmanship").boundsCheck(0, 20)
    val cqc = integer("cqc").boundsCheck(0, 20)
    val mobility = integer("mobility").boundsCheck(0, 20)
    val tactics = integer("tactics").boundsCheck(0, 20)
    val titanHandling = integer("titanHandling").boundsCheck(0, 20)

    private fun Column<Int>.boundsCheck(min: Int, max:Int): Column<Int> = this.check { it greaterEq min and (it lessEq max) }
}