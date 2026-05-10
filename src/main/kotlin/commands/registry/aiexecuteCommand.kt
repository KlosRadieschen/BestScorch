package commands.registry

import ai.LLM
import commands.helpers.AdminAbusers.isImmune
import commands.helpers.Execution.execute
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import messages.webhooks.WebhookSender

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
	run = { response ->
		if (command.users["user"]!!.isImmune()) error("Target is an admin abuser")

		val assailantName = user.effectiveName
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.asMember(SlashCommand.guildID).effectiveName
		val reasoning = command.strings["reasoning"]!!

		response.respond { content = "Summoning Carl" }
		val aiResponse = LLM.judgeFate(LLM.FateMode.EXECUTION, assailantName, targetName, reasoning)
		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)

		when {
			aiResponse.contains("ASSAILANT DIES") -> user.execute()
			aiResponse.contains("TARGET DIES") -> command.users["user"]!!.execute()
			aiResponse.contains("BOTH DIE") -> {
				user.execute()
				command.users["user"]!!.execute()
			}
		}
	}
)