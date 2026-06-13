package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.respondPublic

@Suppress(names = ["unused"])
object TestCommand : SlashCommand(
	name = "test",
	description = "Test if this fucker is online",
	args = {},
	run = {
		respondPublic { content = "https://tenor.com/ss1MoenucUm.gif" }
		null
	}
)