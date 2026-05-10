package messages

import commands.helpers.Execution.isExecuted
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on
			if (author.isBot) return@on

			try {
				ExposerHandler.handleExpose(author, message)

				if (author.isExecuted()) {
					message.delete()
				} else {
					LLMResponder.respond(message)
					RandomReactor.randomReact(kord, message)
					TicTacToeMessages.evaluate(kord, message)
				}
			} catch (e: Exception) {
				val channel = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")))!!
				channel.createMessage("<@384422339393355786> ERROR: ${e.message} <:verger:1225937868023795792>")
			}
		}
	}
}