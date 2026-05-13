package messages.responders

import dev.kord.core.entity.Message

open class Responder(
	val check: Message.() -> Boolean,
	val execute: suspend Message.() -> Unit,
	val executeWithQueue: (suspend Message.() -> Unit)? = null
) {
	suspend fun respond(message: Message) {
		if (message.check()) message.execute()
	}

	suspend fun respondWithQueue(message: Message) {
		if (message.check()) executeWithQueue?.invoke(message) ?: message.execute()
	}
}