package messages

import commands.helpers.Execution
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on
			if (author.isBot) return@on

			if (Execution.isExecuted(author.id)) {
				message.delete()
			} else {
				LLMResponder.respond(message)
				RandomReactor.randomReact(kord, message)
				TicTacToeMessages.evaluate(kord, message)
			}
		}
	}
}