package messages

import ai.LLM
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message

object LLMResponder {
	suspend fun respond(message: Message) {
		if (message.content.contains("Scorch")) {
			message.reply { content = LLM.generateMessage(message.content) }
		}
	}
}