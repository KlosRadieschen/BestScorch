package messages.responders.registry

import ai.CharacterLLM
import ai.helpers.MessageQueue
import ai.systemCharacters.Scorch
import commands.helpers.Execution.isExecuted
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import messages.responders.Responder

object ScorchResponder : Responder(
	check = { (content.lowercase().contains(Regex("(?<!\\\\)\\bscorch\\b")) || referencedMessage?.author?.id == kord.selfId) && !(author?.isExecuted()?:false) },
	execute = {
		val message = this
		channel.withTyping {
			val llmResponse =  Scorch.respond(message)

			reply { content = llmResponse ?: "AI is not responding <:verger:1225937868023795792>" }
		}
	},
	executeWithQueue = {
		val llmResponse = Scorch.respond(this)

		val ref = CharacterLLM.messageQueue.messages.last().msg
		val message = ref.reply { content = llmResponse ?: "AI is not responding <:verger:1225937868023795792>" }
		CharacterLLM.messageQueue.addMessage(message, MessageQueue.Type.UserMessage)
		CharacterLLM.mutex.unlock()
	}
)