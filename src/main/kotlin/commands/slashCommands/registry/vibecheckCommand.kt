package commands.slashCommands.registry

import commands.helpers.Vibechecker.awaitVibe
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.respondPublic

@Suppress("UNUSED")
object VibecheckCommand : SlashCommand(
	name = "vibecheck",
	description = "Check your vibe",
	args = {},
	run = {
		respondPublic {
			files += user.awaitVibe()
		}

		null
	}
)