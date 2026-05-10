package commands.helpers

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import kotlin.time.Instant

data class ExposeMessage(val message: String, val timestamp: Instant)

object Exposer {
	private val messages: MutableMap<Snowflake, ArrayDeque<ExposeMessage>> = mutableMapOf()

	val User.exposeMessages: ArrayDeque<ExposeMessage>
		get() = messages[this.id] ?: error("No saved messages for this user")

	fun User.addExposeMessage(em: ExposeMessage) {
		val queue = messages.getOrPut(this.id) { ArrayDeque() }
		if (queue.size >= 5) queue.removeLast()
		queue.addFirst(em)
	}
}