package commands.registry

import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import io.github.cdimascio.dotenv.Dotenv

object ReviveAllCommand : SlashCommand(
	name = "reviveall",
	description = "UNMURDER everyone",
	args = {},
	run = commandRun@{ response ->
		if (!AdminAbusers.isAdminAbuser(user.id.value)) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		}

		Execution.reviveAll(kord)

		response.respond { content = "Everyone was UNMURDERED" }
	}
)