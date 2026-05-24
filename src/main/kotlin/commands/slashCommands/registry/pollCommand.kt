package commands.slashCommands.registry

import ai.systemCharacters.Hank
import commands.helpers.polls.Poll
import commands.helpers.polls.PollResponses
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
			maxLength = 250
		}
		string("option1", "Option 1") {
			required = true
			maxLength = 200
		}
		string("option2", "Option 2") {
			required = true
			maxLength = 200
		}
		string("option3", "Option 3") { maxLength = 200 }
		string("option4", "Option 4") { maxLength = 200 }
		string("option5", "Option 5") { maxLength = 200 }
		string("option6", "Option 6") { maxLength = 200 }
		string("option7", "Option 7") { maxLength = 200 }
		string("duration", "Default: 1 day")
	},
	run = {
		val response = deferPublicResponse()

		val options = (1..7).mapNotNull { index ->
			command.strings["option$index"]?.takeIf { it.isNotBlank() }
		}

		val poll = Poll(
			command.strings["question"]!!,
			responses = PollResponses.Options(
				values = options,
				votes = IntArray(options.size)
			),
            runCatching {
                Duration.parse(command.strings["duration"] ?: "1d").coerceAtMost(7.days)
            }.getOrElse { Hank.error<IllegalArgumentException>(
	            response,
	            "Invalid time format",
	            "We are currently in the command \"poll\" and the user entered an invalid time format for the duration. Explain ISO-8601 without mentioning its name.") } as Duration
		)

		coroutineScope {
			launch { poll.start(kord, user) }
		}

		response.respond { content = "Poll created" }

		response
	}
)