package messages.responders.registry

import ai.CharacterLLM
import ai.helpers.MessageQueue
import ai.helpers.MessageQueueMessage
import ai.systemCharacters.Scorch
import commands.helpers.Execution.isExecuted
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import messages.responders.Responder

@Suppress("UNUSED")
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
			val message = this
			channel.withTyping {
				val llmResponse = Scorch.respond(message) ?: "AI is not responding <:verger:1225937868023795792>"
				val chunks = llmResponse.chunked(2000)

				val ref = CharacterLLM.messageQueue.messages.last().msg
				val botMessage =
					ref.reply { content = chunks[0] }

				CharacterLLM.messageQueue.addMessage(MessageQueueMessage(botMessage, MessageQueue.Type.AIMessage, Scorch.name))

				if (chunks.size > 1)
					chunks.drop(1).forEach { c -> botMessage.reply { content = c } }
			}
	}
)