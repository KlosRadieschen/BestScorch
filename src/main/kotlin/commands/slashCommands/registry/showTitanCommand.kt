package commands.slashCommands.registry

import Config.Snowflakes.ahaNickname
import ai.systemCharacters.Hank
import commands.slashCommands.SlashCommand
import database.Database
import database.tables.TitanEntity
import database.tables.TitanTable
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
object ShowTitanCommand : SlashCommand (
	name = "show-titan",
	description = "See your or somebody's titan",
	args = {
		string("callsign", "The callsign of the titan") {
			required = true
			autocomplete = true
		}
	},
	run = {
		val response = deferPublicResponse()

		val callsign = command.strings["callsign"]!!

		val titan = transaction(Database.db) {
			TitanEntity.find { TitanTable.callsign eq callsign }.singleOrNull()
		}

		if (titan == null) {
			Hank.error<NoSuchElementException>(
				response,
				"Titan not found",
				"""
					We are in the command "show-titan" which you can use to see your or someone else's titan given their callsign.
					However, the user '${user.ahaNickname()}' just put in a titan callsign that doesn't exist.
					This is despite them having a super convenient and well-programmed autocomplete with all titans in the database.
				""".trimIndent()
			)
		} else {
			response.respond {
				embed {
					color = Color(0xFF69B4)
					title = "${titan.callsign}: ${titan.name ?: ""}"
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
						this.name = kord.getUser(Snowflake(titan.ownerID))!!.ahaNickname()
						icon = kord.getUser(Snowflake(titan.ownerID))!!.avatar?.cdnUrl?.toUrl()
					}

					timestamp = Instant.fromEpochMilliseconds(titan.createdAt.toInstant(TimeZone.UTC).toEpochMilliseconds())
				}

				if (titan.hasLongFields()) actionRow {
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
		}

		response
	}
)