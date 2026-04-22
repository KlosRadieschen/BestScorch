package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user
import io.github.cdimascio.dotenv.Dotenv

val executeCommand = SlashCommand(
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

		executee.asMember(SlashCommand.guildID).addRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		Execution.execute(executee.id.value.toString())

		response.respond { content = "${executee.mention} was MURDERED" }
	}
)