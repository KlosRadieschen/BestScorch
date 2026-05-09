package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond

object ReviveAllCommand : SlashCommand(
	name = "reviveall",
	description = "UNMURDER everyone",
	args = {},
	run = commandRun@{ response ->
		if (!AdminAbusers.isAdminAbuser(user.id)) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		} else if (!Execution.isAnyoneExecuted()) {
			error("Nobody is executed")
		}

		Execution.reviveAll(kord)

		response.respond { content = "Everyone was UNMURDERED" }
	}
)