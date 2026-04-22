package dev.kord.core.slashCommands

import dev.kord.core.Kord
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class SlashCommands() {
	private val commands = ArrayList<SlashCommand>()

	fun add(command: SlashCommand) {
		commands.add(command)
	}

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