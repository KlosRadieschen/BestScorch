package commands.modalCommands.registry

import ai.systemCharacters.Hank
import commands.modalCommands.ModalCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import database.tables.StatsEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException

@Suppress("UNUSED")
object CharacterFormModal : ModalCommand(
	id = "character-form",
	run = {
		val characterID = modalId.split(":")[1].split("-")[0]
		val formChoice = modalId.split(":")[1].split("-")[1]

		try {
			transaction(Database.db) {
				CharacterEntity.findSingleByAndUpdate(CharacterTable.id eq EntityID(characterID.toInt(), CharacterTable) ) {
					when (formChoice) {
						"basic" -> {
							it.age = textInputs["age"]?.value.orEmpty()
							it.gender = textInputs["gender"]?.value.orEmpty()
							it.height = textInputs["height"]?.value.orEmpty()
							it.appearance = textInputs["appearance"]?.value.orEmpty()
						}

						"military" -> {
							it.battalion = textInputs["battalion"]?.value.orEmpty()
							it.rank = textInputs["rank"]?.value.orEmpty()
							it.combatClass = textInputs["combat class"]?.value.orEmpty()
							it.specialties = textInputs["specialties"]?.value.orEmpty()
						}

						"lore" -> {
							it.lore = textInputs["lore"]?.value.orEmpty()
							it.traits = textInputs["traits"]?.value.orEmpty()
							it.likes = textInputs["likes"]?.value.orEmpty()
							it.dislikes = textInputs["dislikes"]?.value.orEmpty()
							it.misc = textInputs["misc"]?.value.orEmpty()
						}

						"stats" -> {
							val stats = it.stats ?: StatsEntity.new { character = it }

							stats.marksmanship = textInputs["marksmanship"]?.value.orEmpty().toIntOrNull() ?: 0
							stats.cqc = textInputs["cqc"]?.value.orEmpty().toIntOrNull() ?: 0
							stats.mobility = textInputs["mobility"]?.value.orEmpty().toIntOrNull() ?: 0
							stats.tactics = textInputs["tactics"]?.value.orEmpty().toIntOrNull() ?: 0
							stats.titanHandling = textInputs["titan handling"]?.value.orEmpty().toIntOrNull() ?: 0
						}
					}
				}
			}
		} catch (_: SQLIntegrityConstraintViolationException) {
			Hank.error<IllegalArgumentException>(
				"All values must be between 0 and 22",
				"The user was setting stats for his OC, but he inputted a value that was outside of the allowed range (0-22)"
			)
		}
	}
)