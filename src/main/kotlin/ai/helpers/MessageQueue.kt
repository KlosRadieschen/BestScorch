package ai.helpers

class MessageQueueMessage(val msg: String, val type: MessageQueue.Type)

class MessageQueue {
	enum class Type {
		UserMessage,
		AIMessage
	}

	companion object {
		const val MAX_MESSAGES = 6
	}

	val messages: ArrayDeque<MessageQueueMessage> = ArrayDeque()

	fun addMessage(msg: String, type: Type) {
		messages.addLast(MessageQueueMessage(msg, type))
		if (messages.size > MAX_MESSAGES) messages.removeFirst()
	}

	fun addMessage(mqm: MessageQueueMessage) {
		messages.addLast( mqm)
		if (messages.size > MAX_MESSAGES) messages.removeFirst()
	}
}