package commands.registry

import commands.ai.LLM
import commands.helpers.AdminAbusers
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.effectiveName
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import io.github.cdimascio.dotenv.Dotenv
import io.swagger.v3.oas.annotations.Webhooks
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
		val assailantName = user.effectiveName
		val targetName = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!.asMember(SlashCommand.guildID).effectiveName
		val reasoning = command.strings["reasoning"]!!

		response.respond { content = "Summoning Carl" }
		val aiResponse = LLM.judgeFate(LLM.FateMode.EXECUTION, assailantName, targetName, reasoning)
		WebhookSender.sendAs(kord, "Carl Jebediah", "https://preview.redd.it/my-favorite-clanker-moment-in-jedi-survivor-v0-wuneb16cbc1b1.jpeg?width=960&format=pjpg&auto=webp&s=82f3e54ad48edc2316b746de7f66da298424890c", aiResponse, channelId)

		when {
			aiResponse.contains("ASSAILANT DIES") -> Execution.execute(user)
			aiResponse.contains("TARGET DIES") -> Execution.execute(command.users["user"]!!)
			aiResponse.contains("BOTH DIE") -> {
				Execution.execute(user)
				Execution.execute(command.users["user"]!!)
			}
		}
	}
)