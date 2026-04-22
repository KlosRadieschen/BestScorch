package dev.kord.core.commands.slashCommands

import dev.kord.core.Kord
import dev.kord.core.commands.registry.*
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class SlashCommands {
	private val commands: Map<String, SlashCommand> = mapOf(
		testCommand.name to testCommand,
		rollCommand.name to rollCommand
	)

	suspend fun createAll(kord: Kord) = commands.values.forEach { it.create(kord) }

	fun registerAll(kord: Kord) {
		kord.on<GuildChatInputCommandInteractionCreateEvent> {
			val response = interaction.deferPublicResponse()
			commands[interaction.data.data.name.value]!!.run(interaction, response)
		}
	}
}