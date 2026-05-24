package commands.modalCommands.registry

import commands.modalCommands.ModalCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object CharacterFormModal : ModalCommand(
	id = "character-form",
	run = {
		val characterName = modalId.split(":")[1].split("-")[0]
		val formChoice = modalId.split(":")[1].split("-")[1]

		transaction(Database.db) {
			CharacterEntity.findSingleByAndUpdate(CharacterTable.sanitizedName eq characterName) {
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
					}

					"lore" -> {
						it.lore = textInputs["lore"]?.value.orEmpty()
						it.traits = textInputs["traits"]?.value.orEmpty()
						it.likes = textInputs["likes"]?.value.orEmpty()
						it.dislikes = textInputs["dislikes"]?.value.orEmpty()
						it.misc = textInputs["misc"]?.value.orEmpty()
					}
				}
			}
		}
	}
)