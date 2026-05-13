package ai.helpers

import dev.kord.core.entity.Message

class MessageQueueMessage(val msg: Message, val type: MessageQueue.Type)

class MessageQueue {
	enum class Type {
		UserMessage,
		AIMessage
	}

	companion object {
		const val MAX_MESSAGES = 10
	}

	val messages: ArrayDeque<MessageQueueMessage> = ArrayDeque()

	fun addMessage(msg: Message, type: Type) {
		messages.addLast(MessageQueueMessage(msg, type))
		if (messages.size > MAX_MESSAGES) messages.removeFirst()
	}

	fun addMessage(mqm: MessageQueueMessage) {
		messages.addLast( mqm)
		if (messages.size > MAX_MESSAGES) messages.removeFirst()
	}
}