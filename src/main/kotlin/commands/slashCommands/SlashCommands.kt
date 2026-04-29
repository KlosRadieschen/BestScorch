package commands.slashCommands

import commands.registry.*
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import kotlin.collections.get

class SlashCommands {
	private val commands: Map<String, SlashCommand> = mapOf(
		testCommand.name to testCommand,
		rollCommand.name to rollCommand,
		executeCommand.name to executeCommand,
		reviveCommand.name to reviveCommand,
		reviveAllCommand.name to reviveAllCommand,
		promoteCommand.name to promoteCommand,
		pollCommand.name to pollCommand,
		discussionPollCommand.name to discussionPollCommand,
	)

	suspend fun createAll(kord: Kord) = commands.values.forEach { it.create(kord) }

	fun registerAll(kord: Kord) {
		kord.on<GuildChatInputCommandInteractionCreateEvent> {
			val response = interaction.deferPublicResponse()
			try {
				commands[interaction.data.data.name.value]!!.run(interaction, response)
			} catch (e: Exception) {
				e.printStackTrace()
				response.respond { content = "${e.message} <:verger:1225937868023795792>" }
			}
		}
	}

	suspend fun deleteOld(kord: Kord) {
		val commands = kord.getGuildApplicationCommands(SlashCommand.guildID)
		commands.collect { command ->
			command.delete()
		}
	}
}