package messages.responders

import dev.kord.core.entity.Message

open class Responder(
	val check: Message.() -> Boolean,
	val execute: suspend Message.() -> Unit
) {
	suspend fun respond(message: Message) {
		if (message.check()) message.execute()
	}
}