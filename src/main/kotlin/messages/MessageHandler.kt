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

			val authorId = author.id.value.toString()
			if (Execution.isExecuted(authorId)) {
				message.delete()
			}
		}
	}
}