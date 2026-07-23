package commands.buttonCommands.registry

import Config
import ai.systemCharacters.Hank
import commands.buttonCommands.ButtonCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import database.tables.StatsEntity
import database.tables.TitanTable.callsign
import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Instant

@Suppress("UNUSED")
object ShowCharacterButton : ButtonCommand (
	id = "show-character",
	run = commandRun@{
		val characterName = componentId.split(":")[1].split("-")[0]
		val formChoice = componentId.split(":")[1].split("-")[1].replaceFirstChar { it.uppercase() }

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.sanitizedName eq characterName }.first()
		}

		if (formChoice == "Showtitan") {
			val response = deferPublicResponse()
			val titan = transaction(Database.db) {
				character.titan
			}

			if (titan == null) Hank.error<NoSuchElementException>(
				response,
				"Titan not found",
				""
			)

			response.respond {
				embed {
					color = Color(0xFF69B4)
					title = "${titan!!.callsign} ${titan.name ?: ""}"
					thumbnail {
						url = titan.image ?: ""
					}

					for (sf in titan.mappedShortFields()) {
						if (!sf.value.isNullOrBlank()) {
							field() {
								this.name = sf.key
								value = sf.value!!
								inline = true
							}
						}
					}

					author {
						this.name = kord.getUser(Snowflake(titan.ownerID))!!.asMember(Config.Snowflakes.ahaGuildID).effectiveName
						icon = kord.getUser(Snowflake(titan.ownerID))!!.avatar?.cdnUrl?.toUrl()
					}

					timestamp = Instant.fromEpochMilliseconds(titan.createdAt.toInstant(TimeZone.UTC).toEpochMilliseconds())
				}

				if (titan!!.hasLongFields()) actionRow {
					for (lf in titan.mappedLongFields()) {
						if (!lf.value.isNullOrBlank()) {
							interactionButton(
								ButtonStyle.Primary,
								"show-titan:$callsign-${lf.key.lowercase()}"
							) {
								label = lf.key
							}
						}
					}
				}
			}

		} else if (formChoice == "Stats") {
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