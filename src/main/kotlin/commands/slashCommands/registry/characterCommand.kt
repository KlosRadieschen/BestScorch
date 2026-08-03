package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.interaction.string
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object CharacterCommand : SlashCommand (
	name = "character",
	description = "Add or edit a new character",
	args = {
		string("name", "The name of the character") {
			required = true
			autocomplete = true
		}
	},
	run = {
		val name = command.strings["name"]!!

		// INSERT IF NOT EXISTS
		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.name eq name }.firstOrNull() ?: CharacterEntity.new {
				this.name = name
				this.ownerID = user.id.value
			}
		}

		val charID = character.id.value

		respondPublic {
			content = """
				# Character Form for "$name" (by ${user.mention})
				
				Welcome to character creation.
				- You can use the buttons below this message to add or edit any information about your OC.
				- You can save and quit at any point, nothing will be lost as long as you click "Submit".
				- You can get this message as many times as you want, you only need to use /character again.
				- When there are multiple messages for the same OC, it doesn't matter where you click the buttons.
				- You can add a PFP with /character-avatar
				
				The buttons lead to the following information:
				- Basic Info: age, gender, height, appearance
				- Military Info: battalion, rank, combat class, specialties
				- Lore Info: lore, traits, likes, dislikes, misc
				- Stats: marksmanship, CQC, mobility, tactics, titan handling
			""".trimIndent()

			actionRow {
				interactionButton(ButtonStyle.Primary, "character-form:$charID-basic") {
					label = "Add basic info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$charID-military") {
					label = "Add military info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$charID-lore") {
					label = "Add lore info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$charID-stats") {
					label = "Add stats"
				}
			}

			actionRow {
				interactionButton(ButtonStyle.Success, "character-form:$charID-create") {
					label = "Create member file"
					disabled = true
				}
			}
		}

		null
	}
)