package messages.responders.registry

import ai.CharacterLLM
import ai.helpers.MessageQueue
import ai.systemCharacters.Scorch
import commands.helpers.Execution.isExecuted
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import messages.responders.Responder

object ScorchResponder : Responder(
	check = { (content.lowercase().contains(Regex("(?<!\\\\)\\bscorch\\b")) || content.contains("<@${kord.selfId}>") || referencedMessage?.author?.id == kord.selfId) && !(author?.isExecuted()?:false) },
	execute = {
		val message = this
		channel.withTyping {
			val llmResponse =  Scorch.respond(message) ?: "AI is not responding <:verger:1225937868023795792>"

			reply { content = llmResponse }
		}
	},
	executeWithQueue = {
		try {
			val message = this
			channel.withTyping {
				val llmResponse = Scorch.respond(message) ?: "AI is not responding <:verger:1225937868023795792>"
				val chunks = llmResponse.chunked(2000)

				val ref = CharacterLLM.messageQueue.messages.last().msg
				val botMessage =
					ref.reply { content = chunks[0] }

				CharacterLLM.messageQueue.addMessage(botMessage, MessageQueue.Type.UserMessage)

				if (chunks.size > 1)
					chunks.drop(1).forEach { c -> botMessage.reply { content = c } }
			}
		} finally {
			CharacterLLM.mutex.unlock()
		}
	}
)