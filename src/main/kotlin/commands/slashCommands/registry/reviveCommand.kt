package commands.slashCommands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution.isExecuted
import commands.helpers.Execution.revive
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.interaction.user
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.io.File

object ReviveCommand : SlashCommand(
	name = "revive",
	description = "UNMURDER",
	args = {
		user("user", "The user to UNMURDER") {
			required = true
		}
	},
	run = { response ->
		if (!user.isAdminAbuser()) error("You are not an admin abuser")
		else if (!command.users["user"]!!.isExecuted()) error("User is not executed")

		val revivee = kord.getUser(command.users["user"]!!.id)!!
		revivee.revive()

		response.respond {
			content = revivee.mention
			files += NamedFile("sick ass revive.gif", ChannelProvider {
				File("src/main/resources/revive.gif").readChannel()
			})
		}
	}
)