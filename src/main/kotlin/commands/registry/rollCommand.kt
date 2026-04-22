package commands.registry

import dev.kord.core.behavior.interaction.response.respond
import commands.slashCommands.SlashCommand
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string

val rollCommand = SlashCommand(
	name = "roll",
	description = "GAMBLING!",
	args = {
		integer("max", "The highest number you can roll (default: 20)") {
			required = false
		}
		string("reason", "What you are rolling for") {
			required = false
		}
		integer("modifier", "The modifier to add to the roll (default: 0)") {
			required = false
		}
	},
	run = { response ->
		val max = command.integers["max"] ?: 20
		val modifier = command.integers["modifier"]
		val reason = command.strings["reason"]

		val baseRoll = (1..max).random()

		val content = buildString {
			if (reason != null) append("Rolling for $reason\n")
			append(
				if (modifier != null) {
					"${baseRoll + modifier} ($baseRoll + $modifier)/$max"
				} else {
					"$baseRoll/$max"
				}
			)
		}

		response.respond { this.content = content }
	}
)