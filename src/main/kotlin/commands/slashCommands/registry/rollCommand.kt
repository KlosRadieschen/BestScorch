package commands.slashCommands.registry

import commands.slashCommands.SlashCommand
import commands.slashCommands.registry.RollCommand.max
import commands.slashCommands.registry.RollCommand.min
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string

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
	run = { response ->
		val advantage = when (command.integers["advantage"] ?: 0) {
			(-1).toLong() -> AdvantageState.Disadvantage
			(0).toLong() -> AdvantageState.None
			(1).toLong() -> AdvantageState.Advantage
            else -> error("This error is literally impossible")
        }

		val max = command.integers["max"] ?: 20
		val modifier = command.integers["modifier"] ?: 0
		val reason = command.strings["reason"]

		val baseRolls = (1..max).random().toInt() to
				if (advantage == AdvantageState.None) null
				else (1..max).random().toInt()

        val roll = when(advantage) {
			AdvantageState.Disadvantage -> baseRolls.min()
			AdvantageState.None -> baseRolls.first
			AdvantageState.Advantage -> baseRolls.max()
		}

		val content = buildString {
			if (reason != null) appendLine("Rolling for: *$reason*")

			appendLine("# ${roll + modifier} / $max")

			if (modifier > 0) appendLine("-# $roll + $modifier")

			if (advantage == AdvantageState.Disadvantage) append("-# With disadvantage: ")
			else if (advantage == AdvantageState.Advantage) append("-# With advantage: ")

			if (advantage != AdvantageState.None) appendLine("${baseRolls.first}, ${baseRolls.second}")
		}

		response.respond { this.content = content }
	}
) {
	enum class AdvantageState {
		Disadvantage, None, Advantage
	}

	private fun Pair<Int, Int?>.min() = minOf(first, second ?: first)
	private fun Pair<Int, Int?>.max() = maxOf(first, second ?: first)
}