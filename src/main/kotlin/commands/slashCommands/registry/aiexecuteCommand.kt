package commands.slashCommands.registry

import Config.Snowflakes.ahaNickname
import ai.systemCharacters.Carl
import ai.systemCharacters.Hank
import commands.helpers.AdminAbusers.isImmune
import commands.helpers.Execution.execute
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import messages.webhooks.WebhookSender

@Suppress("UNUSED")
object AIExecuteCommand : SlashCommand(
	name = "aiexecute",
	description = "Beg Carl to murder your enemies",
	args = {
		user("user", "The user to MURDER") {
			required = true
		}
		string("reasoning", "Why you think the target deserves death") {
			required = true
		}
	},
	run = {
		val response = deferPublicResponse()

		if (command.users["user"]!!.isImmune()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "Target is an admin abuser",
			explanation = """
				We are currently in the command "aiexecute", which is a command that allows the unwashed masses to execute each other using the judgement of an AI god called Carl Jebediah.
				However, the user "${user.effectiveName}" tried to target this command at a member of "High Command", which is an oligarchic circle of admin abusers that are immune to this.
			""".trimIndent()
		)

		val assailantName = user.effectiveName
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.ahaNickname()
		val reasoning = command.strings["reasoning"]!!

		response.respond { content = "Summoning Carl" }

		var aiResponse = Carl.judgeFate(Carl.FateMode.EXECUTION, assailantName, targetName, reasoning) ?: Carl.AWAY_MESSAGE

		when {
			aiResponse.contains("ASSAILANT DIES") -> {
				user.execute()
				aiResponse += " (${user.mention})"
			}
			aiResponse.contains("TARGET DIES") -> {
				command.users["user"]!!.execute()
				aiResponse += " (${command.users["user"]!!.mention})"
			}
			aiResponse.contains("BOTH DIE") -> {
				user.execute()
				command.users["user"]!!.execute()
				aiResponse += " (${user.mention} + ${command.users["user"]!!.mention})"
			}
		}

		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)
		response
	}
)