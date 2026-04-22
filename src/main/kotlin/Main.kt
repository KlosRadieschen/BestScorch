package dev.kord.core

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.slashCommands.SlashCommand
import dev.kord.core.slashCommands.SlashCommands
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.util.valuesOf

suspend fun main() {
	val kord = Kord(Dotenv.load().get("BOT_TOKEN"))

	val commands = kord.getGuildApplicationCommands(SlashCommand.guildID)

	commands.collect { command ->
		command.delete()
	}

	val slashCommands = SlashCommands()

	slashCommands.add(
		SlashCommand(
			name = "test",
			description = "Test if this fucker is online",
			args = {},
			run = { response ->
				response.respond { content = "https://tenor.com/ss1MoenucUm.gif" }
			}
		)
	)

	slashCommands.add(
		SlashCommand(
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
				//integer("advantage", "You can roll with advantage or disadvantage") {
				//	required = false
				//}
			},
			run = { response ->
				val max = command.integers["max"] ?: 20
				val baseRoll = (1..max).random()

				var finalRoll: String
				val modifier = command.integers["modifier"]
				finalRoll = if (modifier != null) {
					"${baseRoll + modifier}($baseRoll + $modifier)/$max"
				} else {
					"$baseRoll/$max"
				}

				val reason = command.strings["reason"]
				if (reason != null) {
					response.respond { content = "Rolling for $reason\n$finalRoll" }
				} else {
					response.respond { content = finalRoll }
				}
			}
		)
	)

	slashCommands.createAll(kord)
	slashCommands.registerAll(kord)

	kord.login {
		presence { playing("I was forced to do this") }

		@OptIn(PrivilegedIntent::class)
		intents += Intent.MessageContent
	}
}