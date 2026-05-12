package commands.slashCommands.registry

import ai.systemCharacters.Carl
import commands.helpers.Execution.execute
import commands.helpers.Execution.revive
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
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.asMember(guildID).effectiveName
		val reasoning = command.strings["reasoning"]!!

		response.respond { content = "Summoning Carl" }

		val aiResponse = Carl.judgeFate(Carl.FateMode.REVIVAL, beggarName, targetName, reasoning) ?: Carl.AWAY_MESSAGE

		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)

		when {
			aiResponse.contains("REVIVE") -> command.users["user"]!!.revive()
			aiResponse.contains("DIE FOR TRYING") -> user.execute()
		}
	}
)