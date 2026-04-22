package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user
import io.github.cdimascio.dotenv.Dotenv

val reviveCommand = SlashCommand(
	name = "revive",
	description = "UNMURDER",
	args = {
		user("user", "The user to UNMURDER") {
			required = true
		}
	},
	run = { response ->
		if (!AdminAbusers.isAdminAbuser(user.id.value)) {
			response.respond { content = "You are not an admin abuser" }
			return@SlashCommand
		}

		val revivee = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!

		revivee.asMember(SlashCommand.guildID).removeRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		Execution.revive(revivee.id.value.toString())

		response.respond { content = "${revivee.mention} was UNMURDERED" }
	}
)