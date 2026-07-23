package commands.slashCommands.registry

import Config
import ai.systemCharacters.Hank
import commands.slashCommands.SlashCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Instant

@Suppress("UNUSED")
object ShowCharacterCommand : SlashCommand (
	name = "show-character",
	description = "See your or somebody's character",
	args = {
		string("name", "The name of the character") {
			required = true
			autocomplete = true
		}
	},
	run = {
		val response = deferPublicResponse()

		val name = command.strings["name"]!!
		val sanitizedName = name.lowercase().replace(" ", "_")

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.sanitizedName eq sanitizedName }.singleOrNull()
		}

		if (character == null) {
			Hank.error<NoSuchElementException>(
				response,
				"Character not found",
				"""
					We are in the command "show-character" which you can use to see your or someone else's character given their name.
					However, the user '${user.asMember(Config.Snowflakes.ahaGuildID).effectiveName}' just put in a character that doesn't exist.
					This is despite them having a super convenient and well-programmed autocomplete with all characters in the database.
				""".trimIndent()
			)
		} else {
			response.respond {
				embed {
					color = Color(0xFF69B4)
					title = character.name
					thumbnail {
						url = character.image ?: ""
					}

					for (sf in character.mappedShortFields()) {
						if (!sf.value.isNullOrBlank()) {
							field() {
								this.name = sf.key
								value = sf.value!!
								inline = true
							}
						}
					}

					author {
						this.name = kord.getUser(Snowflake(character.ownerID))!!.asMember(Config.Snowflakes.ahaGuildID).effectiveName
						icon = kord.getUser(Snowflake(character.ownerID))!!.avatar?.cdnUrl?.toUrl()
					}

					timestamp = Instant.fromEpochMilliseconds(character.createdAt.toInstant(TimeZone.UTC).toEpochMilliseconds())
				}

				transaction {
					if (character.hasLongFields() || character.stats != null) actionRow {
						if (character.hasLongFields()) {
							for (lf in character.mappedLongFields()) {
								if (!lf.value.isNullOrBlank()) {
									interactionButton(
										ButtonStyle.Primary,
										"show-character:$sanitizedName-${lf.key.lowercase()}"
									) {
										label = lf.key
									}
								}
							}
						}

						if (character.stats != null) {
							interactionButton(
								ButtonStyle.Primary,
								"show-character:$sanitizedName-stats"
							) {
								label = "Stats"
							}
						}
					}
				}

				transaction {
					if (character.titan != null) actionRow {
						interactionButton(ButtonStyle.Primary, "show-character:$sanitizedName-showtitan") {
							label = "Titan"
						}
					}
				}
			}
		}

		response
	}
)