package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user

object ExecuteCommand : SlashCommand(
	name = "execute",
	description = "MURDER",
	args = {
		user("user", "The user to MURDER") {
			required = true
		}
	},
	run = { response ->
		val target = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!
		val executee = if (AdminAbusers.isAdminAbuser(user.id.value)) target else user

		Execution.execute(executee)

		response.respond { content = "${executee.mention} was MURDERED" }
	}
)