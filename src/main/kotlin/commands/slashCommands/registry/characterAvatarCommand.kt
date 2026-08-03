package commands.slashCommands.registry

import Config.Snowflakes.ahaNickname
import ai.systemCharacters.Hank
import commands.slashCommands.SlashCommand
import commands.slashCommands.registry.CharacterAvatarCommand.rehostImage
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.rest.builder.interaction.attachment
import dev.kord.rest.builder.interaction.string
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object CharacterAvatarCommand : SlashCommand (
	name = "character-avatar",
	description = "Add an image to your character",
	args = {
		string("name", "The name of your character") {
			required = true
			autocomplete = true
		}
		attachment("image", "The image for your character") {
			required = true
		}
	},
	run = commandRun@{
		val response = deferPublicResponse()

		if (!(command.attachments["image"]?.isImage ?: false)) {
			Hank.error<IllegalArgumentException>(
				defaultMsg = "The attachment must be an image",
				explanation = """
					We are in he command "character-avatar" where a user can upload an avatar/image for their character.
					However, the user ${user.ahaNickname()} tried to upload a non-image attachment.
				""".trimIndent(),
				response = response
			)
		}

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.name eq command.strings["name"]!! }.firstOrNull()
		}

		if (character == null) {
			Hank.error<IllegalArgumentException>(
				defaultMsg = "The attachment must be an image",
				explanation = """
					We are in he command "character-avatar" where a user can upload an avatar/image for their character.
					However, the user ${user.ahaNickname()} selected a character that doesn't exist.
				""".trimIndent(),
				response = response
			)
		}

		val msg = rehostImage(response, command.attachments["image"]!!.url, "${character!!.id}-avatar.png")

		transaction(Database.db) {
			CharacterEntity.findSingleByAndUpdate( CharacterTable.name eq command.strings["name"]!! ) {
				it.image = msg.message.attachments.first().url
			}
		}

		response
	}
) {
	private suspend fun rehostImage(response: DeferredPublicMessageInteractionResponseBehavior, url: String, filename: String): PublicMessageInteractionResponse {
		val client = HttpClient(CIO)

		client.use { client ->
			val bytes = client.get(url).bodyAsBytes()

			return response.respond {
				addFile(filename, ChannelProvider {
					ByteReadChannel(bytes)
				})
			}
		}
	}
}