package commands.slashCommands.registry

import Config
import ai.systemCharacters.Carl
import ai.systemCharacters.Hank
import commands.helpers.Execution.execute
import commands.helpers.Execution.isExecuted
import commands.helpers.Execution.revive
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import messages.webhooks.WebhookSender

@Suppress("UNUSED")
object AIReviveCommand : SlashCommand(
	name = "airevive",
	description = "Beg Carl to revive your friends",
	args = {
		user("user", "The user to UNMURDER") {
			required = true
		}
		string("reasoning", "Why you think the target deserves life") {
			required = true
		}
	},
	run = {
		val response = deferPublicResponse()

		val beggarName = user.effectiveName
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.asMember(Config.Snowflakes.ahaGuildID).effectiveName
		val reasoning = command.strings["reasoning"]!!

		if (!command.users["user"]!!.isExecuted()) Hank.error<IllegalArgumentException>(
			response,
			defaultMsg = "Nobody is executed",
			explanation = """
				We are currently in the command "airevive", which is a command that allows the unwashed masses to revive each other using the judgement of an AI god called Carl Jebediah.
				However, "${user.effectiveName}" tried to use this command on someone who is not executed.
			""".trimIndent()
		)

		response.respond { content = "Summoning Carl" }

		var aiResponse = Carl.judgeFate(Carl.FateMode.REVIVAL, beggarName, targetName, reasoning) ?: Carl.AWAY_MESSAGE

		when {
			aiResponse.contains("SOUL TRADE") -> {
				user.execute()
				command.users["user"]!!.revive()
				aiResponse += " (${user.mention} + ${command.users["user"]!!.mention})"
			}
			aiResponse.contains("REVIVE") && !aiResponse.contains("NO REVIVE") -> {
				command.users["user"]!!.revive()
				aiResponse += " (${command.users["user"]!!.mention})"
			}
			aiResponse.contains("DIE FOR TRYING") -> {
				user.execute()
				aiResponse += " (${user.mention})"
			}
		}

		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)
		response
	}
)