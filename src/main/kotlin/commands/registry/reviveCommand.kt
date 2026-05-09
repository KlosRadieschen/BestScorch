package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
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
		if (!AdminAbusers.isAdminAbuser(user.id.value)) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		} else if (!Execution.isExecuted(command.users["user"]!!.id.value.toString())) {
			error("User is not executed")
		}

		val revivee = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!
		Execution.revive(revivee)

		response.respond { content = "${revivee.mention} was UNMURDERED" }
	}
)