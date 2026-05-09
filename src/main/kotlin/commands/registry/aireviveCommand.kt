package commands.registry

import ai.LLM
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import messages.webhooks.WebhookSender

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
	run = { response ->
		val beggarName = user.effectiveName
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.asMember(SlashCommand.guildID).effectiveName
		val reasoning = command.strings["reasoning"]!!

		response.respond { content = "Summoning Carl" }
		val aiResponse = LLM.judgeFate(LLM.FateMode.REVIVAL, beggarName, targetName, reasoning)
		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)

		when {
			aiResponse.contains("REVIVE") -> Execution.revive(command.users["user"]!!)
			aiResponse.contains("DIE FOR TRYING") -> Execution.execute(user)
		}
	}
)