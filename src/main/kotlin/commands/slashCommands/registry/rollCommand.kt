package commands.slashCommands.registry

import ai.systemCharacters.Hank
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.boolean
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
		integer("dice-amount", "The amount of dice") {
			required = false
			minValue = 1
		}
		boolean("advantage", "Whether to use dice-amount as advantage") {
			required = false
		}
	},
	run = { response ->
		val diceAmount: Int = (command.integers["dice-amount"] ?: 1).toInt()
		val max = command.integers["max"] ?: 20
		val modifier = command.integers["modifier"] ?: 0
		val reason = command.strings["reason"]
		val advantage = command.booleans["advantage"] ?: false

		if (advantage && diceAmount == 1) Hank.error<IllegalArgumentException>("Advantage set without dice-amount", """
			We are in the command "roll" which allows to throw dice in many different ways.
			However, the user "${user.effectiveName}" set the option "advantage" without setting the option "dice-amount".
			"advantage" can only be used when "dice-amount" is set.
		""".trimIndent())

		val baseRolls: MutableList<Int> = mutableListOf()
		repeat(diceAmount) {
			baseRolls.add((1..max).random().toInt())
		}

		val joinedRolls = baseRolls.joinToString(" + ")
		val sumOrMax = if (!advantage) baseRolls.sum() else baseRolls.max()
		val rollMax = if (!advantage) max * diceAmount else max

		val content = buildString {
			if (reason != null) appendLine("Rolling for $reason")

			append(sumOrMax + modifier)
			if (modifier > 0) append(" ($sumOrMax + $modifier)")
			appendLine(" / $rollMax")

			if (diceAmount > 1) append("-# Individual d$max: $joinedRolls")
			append( if (advantage) " (Advantage)" else " (Sum)" )
		}

		response.respond { this.content = content }
	}
)