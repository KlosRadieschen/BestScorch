package commands.buttonCommands.registry

import commands.buttonCommands.ButtonCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondEphemeral
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object CharacterFormButton : ButtonCommand (
	id = "character-form",
	run = commandRun@{
		val characterName = componentId.split(":")[1].split("-")[0]
		val formChoice = componentId.split(":")[1].split("-")[1]

		if (formChoice == "create") {
			respondEphemeral { content = "Not implemented yet" }
			return@commandRun
		}

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.sanitizedName eq characterName }.first()
		}

		if (character.ownerID != user.id.value) {
			respondEphemeral { content = "You don't own this character, bozo" }
			return@commandRun
		}

		modal("$formChoice Info Form", componentId) {
			fun addModalInput(
				label: String,
				style: TextInputStyle,
				currentValue: String?
			) {
				label(label) {
					textInput(style, label.lowercase()) {
						value = currentValue.orEmpty()
						placeholder = currentValue.orEmpty()
						required = false
					}
				}
			}

			when (formChoice) {
				"basic" -> {
					addModalInput("Age", TextInputStyle.Short, character.age)
					addModalInput("Gender", TextInputStyle.Short, character.gender)
					addModalInput("Height", TextInputStyle.Short, character.height)
					addModalInput("Appearance", TextInputStyle.Paragraph, character.appearance)
				}

				"military" -> {
					addModalInput("Battalion", TextInputStyle.Short, character.battalion)
					addModalInput("Rank", TextInputStyle.Short, character.rank)
					addModalInput("Combat Class", TextInputStyle.Short, character.combatClass)
					addModalInput("Specialties", TextInputStyle.Short, character.specialties)
				}

				"lore" -> {
					addModalInput("Lore", TextInputStyle.Paragraph, character.lore)
					addModalInput("Traits", TextInputStyle.Paragraph, character.traits)
					addModalInput("Likes", TextInputStyle.Short, character.likes)
					addModalInput("Dislikes", TextInputStyle.Short, character.dislikes)
					addModalInput("Misc", TextInputStyle.Paragraph, character.misc)
				}

				"stats" -> {
					transaction(Database.db) {
						addModalInput("Marksmanship", TextInputStyle.Short, character.stats?.marksmanship?.toString() ?: "")
						addModalInput("CQC", TextInputStyle.Short, character.stats?.cqc?.toString() ?: "")
						addModalInput("Mobility", TextInputStyle.Short, character.stats?.mobility?.toString() ?: "")
						addModalInput("Tactics", TextInputStyle.Short, character.stats?.tactics?.toString() ?: "")
						addModalInput("Titan Handling", TextInputStyle.Short, character.stats?.titanHandling?.toString() ?: "")
					}
				}
			}
		}
	}
)