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
				
				Welcome to titan creation.
				- You can use the buttons below this message to add or edit any information about your titan.
				- You can save and quit at any point, nothing will be lost as long as you click "Submit".
				- You can get this message as many times as you want, you only need to use /titan again.
				- When there are multiple messages for the same OC, it doesn't matter where you click the buttons.
				- You can assign your titan to any OC with /assign-titan
				
				The buttons lead to the following information:
				- Basic Info: name, chassis, class, kits
				- Military Info: primary weapon, ordinance, defensive ability, tactical ability, core
				- Lore Info: description, design/appearance, misc
				
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