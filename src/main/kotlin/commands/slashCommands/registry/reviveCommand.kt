package commands.slashCommands.registry

import ai.systemCharacters.Hank
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
		if (!user.isAdminAbuser()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "You are not an admin abuser",
			explanation = """
				We are currently in the command "revive", which is a command that allows the "High Command" (an oligarchic circle of admin abusers) revive previously executed members of the server.
				However, the filthy peasant "${user.effectiveName}" tried to use this command without being a member of "High Command".
			""".trimIndent()
		)
		else if (!command.users["user"]!!.isExecuted()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "Nobody is executed",
			explanation = """
				We are currently in the command "revive", which is a command that allows the "High Command" (an oligarchic circle of admin abusers) to promote or demote members of the server.
				However, "${user.effectiveName}" tried to use this command on someone who is not executed.
			""".trimIndent()
		)

		val revivee = kord.getUser(command.users["user"]!!.id)!!
		revivee.revive()

		response.respond {
			content = revivee.mention
			files += NamedFile("sick ass revive.gif", ChannelProvider {
				File("src/main/resources/reviveLowQual.gif").readChannel()
			})
		}
	}
)