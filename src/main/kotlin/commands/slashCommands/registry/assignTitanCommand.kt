package commands.slashCommands.registry

import Config.Snowflakes.ahaNickname
import ai.systemCharacters.Hank
import commands.slashCommands.SlashCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import database.tables.TitanEntity
import database.tables.TitanTable
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object AssignTitanCommand : SlashCommand (
	name = "assign-titan",
	description = "Assign your titan to a character",
	args = {
		string("callsign", "Callsign of the titan") {
			required = true
			autocomplete = true
		}
		string("character", "The name of the character") {
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

		if (titan == null) Hank.error<NoSuchElementException>(
			response,
			"Titan not found",
			"""
				We are in the command "assign-titan" which you can use to see your or someone else's titan given their callsign.
				However, the user '${user.ahaNickname()}' just put in a titan callsign that doesn't exist.
				This is despite them having a super convenient and well-programmed autocomplete with all titans in the database.
			""".trimIndent()
		)

		val name = command.strings["character"]!!

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.name eq name }.singleOrNull()
		}

		if (character == null) Hank.error<NoSuchElementException>(
			response,
			"Character not found",
			"""
				We are in the command "show-character" which you can use to see your or someone else's character given their name.
				However, the user '${user.ahaNickname()}' just put in a character that doesn't exist.
				This is despite them having a super convenient and well-programmed autocomplete with all characters in the database.
			""".trimIndent()
		)

		transaction(Database.db) {
			titan!!.character = character
		}

		response.respond { content = "Titan assigned" }
		response
	}
)