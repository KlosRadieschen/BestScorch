package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import commands.slashCommands.registry.RollCommand.max
import commands.slashCommands.registry.RollCommand.min
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import kotlin.math.absoluteValue

@Suppress("UNUSED")
object RollCommand : SlashCommand(
	name = "roll",
	description = "GAMBLING!",
	args = {
		string("reason", "What you are rolling for") {
			required = false
		}
		integer("max", "The highest number you can roll (default: 20)") {
			required = false
			minValue = 1
		}
		integer("modifier", "The modifier to add to the roll (default: 0)") {
			required = false
		}
		integer("advantage", "Whether to use dice-amount as advantage") {
			required = false

			choice("Disadvantage", -1)
			choice("None", 0)
			choice("Advantage", 1)
		}
	},
	run = {
		val advantage = when (command.integers["advantage"] ?: 0) {
			(-1).toLong() -> AdvantageState.Disadvantage
			(0).toLong() -> AdvantageState.None
			(1).toLong() -> AdvantageState.Advantage
            else -> error("This error is literally impossible")
        }

		val max = (command.integers["max"] ?: 20)
		val modifier = (command.integers["modifier"] ?: 0)
		val reason = command.strings["reason"]

		val baseRolls = (1..max).random() to
				if (advantage == AdvantageState.None) null
				else (1..max).random()

        val roll = when(advantage) {
			AdvantageState.Disadvantage -> baseRolls.min()
			AdvantageState.None -> baseRolls.first
			AdvantageState.Advantage -> baseRolls.max()
		}

		val content = buildString {
			if (reason != null) appendLine("Rolling for: *$reason*")

			appendLine("# ${roll + modifier} / $max")

			if (modifier > 0) appendLine("-# $roll + $modifier")
			if (modifier < 0) appendLine("-# $roll - ${modifier.absoluteValue}")

			if (advantage == AdvantageState.Disadvantage) append("-# With disadvantage: ")
			else if (advantage == AdvantageState.Advantage) append("-# With advantage: ")

			if (advantage != AdvantageState.None) appendLine("${baseRolls.first}, ${baseRolls.second}")
		}

		respondPublic { this.content = content }

		null
	}
) {
	enum class AdvantageState {
		Disadvantage, None, Advantage
	}

	private fun Pair<Long, Long?>.min() = minOf(first, second ?: first)
	private fun Pair<Long, Long?>.max() = maxOf(first, second ?: first)
}