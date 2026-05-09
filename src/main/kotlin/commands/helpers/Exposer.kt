package commands.helpers

import dev.kord.common.entity.Snowflake

object Exposer {
	private val messages: MutableMap<Snowflake, ArrayDeque<String>> = mutableMapOf()

	fun push(key: Snowflake, value: String) {
		val queue = messages.getOrPut(key) { ArrayDeque() }
		if (queue.size >= 5) queue.removeLast()
		queue.addFirst(value)
	}

	fun getAll(key: Snowflake): List<String> = messages[key]?.toList() ?: emptyList()
}