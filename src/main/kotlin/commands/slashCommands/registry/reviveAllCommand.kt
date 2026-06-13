package commands.slashCommands.registry

import ai.systemCharacters.Hank
import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.NamedFile
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.io.File

@Suppress("UNUSED")
object ReviveAllCommand : SlashCommand(
	name = "reviveall",
	description = "UNMURDER everyone",
	args = {},
	run = {
		val response = deferPublicResponse()

		if (!user.isAdminAbuser()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "You are not an admin abuser",
			explanation = """
				We are currently in the command "reviveall", which is a command that allows the "High Command" (an oligarchic circle of admin abusers) revive previously executed members of the server.
				However, the filthy peasant "${user.effectiveName}" tried to use this command without being a member of "High Command".
			""".trimIndent()
		)
		else if (!Execution.isAnyoneExecuted()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "Nobody is executed",
			explanation = """
				We are currently in the command "reviveall", which is a command that allows the "High Command" (an oligarchic circle of admin abusers) to promote or demote members of the server.
				However, "${user.effectiveName}" tried to use this command even though nobody is executed.
			""".trimIndent()
		)

		response.respond {
			content = buildString {
				Execution.executees.forEach { append(kord.getUser(it)!!.mention) }
			}
			files += NamedFile("sick ass revive.gif", ChannelProvider {
				File("src/main/resources/revive.gif").readChannel()
			})
		}

		Execution.reviveAll(kord)

		response
	}
)