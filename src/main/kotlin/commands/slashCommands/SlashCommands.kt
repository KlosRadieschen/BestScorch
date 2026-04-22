package dev.kord.core.commands.slashCommands

import dev.kord.core.Kord
import dev.kord.core.commands.registry.*
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class SlashCommands {
	private val commands: ArrayList<SlashCommand> = listOf(
		testCommand,
		rollCommand
	) as ArrayList<SlashCommand>

	suspend fun createAll(kord: Kord) {
		for (command in commands) {
			command.create(kord)
		}
	}

	fun registerAll(kord: Kord) {
		kord.on<GuildChatInputCommandInteractionCreateEvent> {
			val response = interaction.deferPublicResponse()

			for (command in commands) {
				if (interaction.data.data.name.value.equals(command.name)) {
					command.run(interaction, response)
				}
			}
		}
	}
}