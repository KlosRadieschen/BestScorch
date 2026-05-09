package commands.helpers

import dev.kord.common.entity.Snowflake
import kotlin.time.Instant

data class ExposeMessage(val message: String, val timestamp: Instant)

object Exposer {
	private val messages: MutableMap<Snowflake, ArrayDeque<ExposeMessage>> = mutableMapOf()

	fun push(key: Snowflake, em: ExposeMessage) {
		val queue = messages.getOrPut(key) { ArrayDeque() }
		if (queue.size >= 5) queue.removeLast()
		queue.addFirst(em)
	}

	fun getAll(key: Snowflake): List<ExposeMessage> = messages[key]?.toList() ?: emptyList()
}