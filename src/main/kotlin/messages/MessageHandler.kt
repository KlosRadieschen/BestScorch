package messages

import commands.helpers.Execution
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			if (message.author?.isBot == true) return@on
			if (Execution.isExecuted(message.author?.id?.value.toString())) message.delete()
		}
	}
}