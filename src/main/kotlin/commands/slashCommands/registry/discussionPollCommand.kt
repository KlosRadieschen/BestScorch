package commands.slashCommands.registry

import commands.polls.Poll
import commands.polls.PollResponses
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration


object DiscussionPollCommand : SlashCommand(
	name = "discussion-poll",
	description = "Create the best polls on Discord",
	args = {
		string("question", "Pretty self-explanatory") {
			required = true
			maxLength = 256
		}
	},
	run = { response ->
		val poll = Poll(
			command.strings["question"]!!,
			responses = PollResponses.Discussion,
			Duration.parse(command.strings["duration"] ?: "1d")
		)

		coroutineScope {
			launch { poll.start(kord, user) }
			response.respond { content = "Poll created" }
		}
	}
)