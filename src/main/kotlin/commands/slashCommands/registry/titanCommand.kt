package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import database.Database
import database.tables.TitanEntity
import database.tables.TitanTable
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.interaction.string
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object TitanCommand : SlashCommand (
	name = "titan",
	description = "Add or edit a new titan",
	args = {
		string("callsign", "Callsign of the titan") {
			required = true
			autocomplete = true
		}
	},
	run = {
		val callsign = command.strings["callsign"]!!

		// INSERT IF NOT EXISTS
		transaction(Database.db) {
			TitanEntity.find { TitanTable.callsign eq callsign }.firstOrNull() ?: TitanEntity.new {
				this.callsign = callsign
				this.ownerID = user.id.value
			}
		}

		respondPublic {
			content = """
				# Titan Form for "$callsign" (by ${user.mention})
				
				- Basic Info: age, gender, height, appearance
				- Military Info: battalion, rank, combat class, specialties
				- Lore Info: lore, traits, likes, dislikes, misc
				
			""".trimIndent()

			actionRow {
				interactionButton(ButtonStyle.Primary, "titan-form:$callsign~basic") {
					label = "Add basic info"
				}

				interactionButton(ButtonStyle.Primary, "titan-form:$callsign~military") {
					label = "Add military info"
				}

				interactionButton(ButtonStyle.Primary, "titan-form:$callsign~lore") {
					label = "Add lore info"
				}
			}
		}

		null
	}
)