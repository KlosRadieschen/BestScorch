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
		val sanitizedName = name.lowercase().replace(" ", "_")

		// INSERT IF NOT EXISTS
		transaction(Database.db) {
			CharacterEntity.find { CharacterTable.name eq name }.firstOrNull() ?: CharacterEntity.new {
				this.name = name
				this.sanitizedName = sanitizedName
				this.ownerID = user.id.value
			}
		}

		respondPublic {
			content = """
				# Character Form for "$name" (by ${user.mention})
				
				- Basic Info: age, gender, height, appearance
				- Military Info: battalion, rank, combat class, specialties
				- Lore Info: lore, traits, likes, dislikes, misc
				
			""".trimIndent()

			actionRow {
				interactionButton(ButtonStyle.Primary, "character-form:$sanitizedName-basic") {
					label = "Add basic info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$sanitizedName-military") {
					label = "Add military info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$sanitizedName-lore") {
					label = "Add lore info"
				}

				interactionButton(ButtonStyle.Primary, "character-form:$sanitizedName-stats") {
					label = "Add Stats"
				}
			}

			actionRow {
				interactionButton(ButtonStyle.Success, "character-form:$sanitizedName-create") {
					label = "Create member file"
				}
			}
		}

		null
	}
)