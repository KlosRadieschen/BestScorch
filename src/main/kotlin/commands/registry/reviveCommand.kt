package commands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution.isExecuted
import commands.helpers.Execution.revive
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user

object ReviveCommand : SlashCommand(
	name = "revive",
	description = "UNMURDER",
	args = {
		user("user", "The user to UNMURDER") {
			required = true
		}
	},
	run = commandRun@{ response ->
		if (!user.isAdminAbuser()) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		} else if (!command.users["user"]!!.isExecuted()) {
			error("User is not executed")
		}

		val revivee = kord.getUser(command.users["user"]!!.id)!!
		revivee.revive()

		response.respond { content = "${revivee.mention} was UNMURDERED" }
	}
)