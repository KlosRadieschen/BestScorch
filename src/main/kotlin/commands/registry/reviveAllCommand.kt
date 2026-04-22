package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import io.github.cdimascio.dotenv.Dotenv

val reviveAllCommand = SlashCommand(
	name = "reviveall",
	description = "UNMURDER everyone",
	args = {},
	run = { response ->
		if (!AdminAbusers.isAdminAbuser(user.id.value)) {
			response.respond { content = "You are not an admin abuser" }
			return@SlashCommand
		}

		Execution.executees.forEach { executee ->
			val revivee = kord.getUser(Snowflake(executee))!!
			revivee.asMember(SlashCommand.guildID).removeRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		}
		Execution.reviveAll()

		response.respond { content = "Everyone was UNMURDERED" }
	}
)