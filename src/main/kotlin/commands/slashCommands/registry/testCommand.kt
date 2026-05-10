package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond

object TestCommand : SlashCommand(
	name = "test",
	description = "Test if this fucker is online",
	args = {},
	run = { response ->
		response.respond { content = "https://tenor.com/ss1MoenucUm.gif" }
	}
)