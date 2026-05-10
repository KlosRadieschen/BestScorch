package commands.slashCommands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.NamedFile
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.io.File

object ReviveAllCommand : SlashCommand(
	name = "reviveall",
	description = "UNMURDER everyone",
	args = {},
	run = commandRun@{ response ->
		if (!user.isAdminAbuser()) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		} else if (!Execution.isAnyoneExecuted()) {
			error("Nobody is executed")
		}

		Execution.reviveAll(kord)

		response.respond {
			content = buildString {
				Execution.executees.forEach { append(kord.getUser(it)!!.mention) }
			}
			files += NamedFile("sick ass revive.gif", ChannelProvider {
				File("src/main/resources/revive.gif").readChannel()
			})
		}
	}
)