package dev.kord.core.commands.registry

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.commands.slashCommands.SlashCommand

val testCommand = SlashCommand(
	name = "test",
	description = "Test if this fucker is online",
	args = {},
	run = { response ->
		response.respond { content = "https://tenor.com/ss1MoenucUm.gif" }
	}
)