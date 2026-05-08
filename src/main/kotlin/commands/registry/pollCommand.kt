package commands.registry

import commands.polls.Poll
import commands.polls.PollResponses
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.string
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days


object PollCommand : SlashCommand(
	name = "poll",
	description = "Create the best polls on Discord",
	args = {
		string("question", "Pretty self-explanatory") {
			required = true
		}
		string("option1", "Option 1") {
			required = true
		}
		string("option2", "Option 2") {
			required = true
		}
		string("option3", "Option 3")
		string("option4", "Option 4")
		string("option5", "Option 5")
		string("option6", "Option 6")
		string("option7", "Option 7")
		string("duration", "Default: 1 day")
	},
	run = { response ->
		val options = (1..7).mapNotNull { index ->
			command.strings["option$index"]?.takeIf { it.isNotBlank() }
		}

		val poll = Poll(
			command.strings["question"]!!,
			responses = PollResponses.Options(
				values = options,
				votes = IntArray(options.size)
			),
			Duration.parse(command.strings["duration"] ?: "1d").coerceAtMost(7.days)
		)

		coroutineScope {
			launch { poll.start(kord, user) }
			response.respond { content = "Poll created" }
		}
	}
)