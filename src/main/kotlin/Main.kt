package dev.kord.core

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.slashCommands.SlashCommand
import dev.kord.core.slashCommands.SlashCommands
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv

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

	slashCommands.createAll(kord)
	slashCommands.registerAll(kord)

	kord.login {
		presence { playing("I was forced to do this") }

		@OptIn(PrivilegedIntent::class)
		intents += Intent.MessageContent
	}
}