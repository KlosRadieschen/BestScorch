package commands.buttonCommands.registry

import commands.buttonCommands.ButtonCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import database.tables.StatsEntity
import dev.kord.common.Color
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.rest.builder.message.embed
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object ShowCharacterButton : ButtonCommand (
	id = "show-character",
	run = commandRun@{
		val characterName = componentId.split(":")[1].split("-")[0]
		val formChoice = componentId.split(":")[1].split("-")[1].replaceFirstChar { it.uppercase() }

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.sanitizedName eq characterName }.first()
		}

		if (formChoice == "Stats") {
			respondEphemeral {
				embed {
					color = Color(0xFF69B4)
					title = "$formChoice for '${character.name}'"

					var sum = 0
					transaction {
						for (stat in character.stats!!.mappedFields()) {
							field {
								this.name = stat.key
								value = "${stat.value.toString()} (${StatsEntity.numberToGrade(stat.value ?: 0)})"
								inline = true
							}

							sum += stat.value ?: 0
						}

						field {
							this.name = "Total points used"
							value = sum.toString()
							inline = true
						}
					}
				}
			}
		} else {
			val fieldValue = character.mappedLongFields()[formChoice]

			respondEphemeral {
				embed {
					color = Color(0xFF69B4)
					title = "$formChoice for '${character.name}'"
					description = fieldValue
				}
			}
		}

	}
)