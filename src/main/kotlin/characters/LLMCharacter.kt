package characters

import ai.CharacterLLM
import ai.helpers.MessageQueue
import commands.helpers.Execution.isExecuted
import messages.responders.Responder
import messages.webhooks.WebhookSender.sendAs

abstract class LLMCharacter (
	override val name: String,
	override val pfp: String,
	val intro: String,
	private val llm: CharacterLLM = CharacterLLM(name, intro)
) : Character (
	name,
	pfp,
	responder = Responder(
		check = { (content.lowercase().contains(Regex("(?<!\\\\)\\b${name.lowercase()}\\b")) || referencedMessage?.data?.author?.username == name) && !(author?.isExecuted()?:false) },
		execute = {
			sendAs(
				kord,
				name = name,
				profilePictureLink = pfp,
				message = llm.respond(this) ?: "AI is currently not enabled",
				channelID = channelId,
				this
			)
		},
		executeWithQueue = {
			val llmResponse = llm.respond(this) ?: "AI is currently not enabled"

			val message = sendAs(
				kord,
				name = name,
				profilePictureLink = pfp,
				message = llmResponse,
				channelID = channelId,
				CharacterLLM.messageQueue.messages.last().msg
			)

			CharacterLLM.messageQueue.addMessage(message, MessageQueue.Type.UserMessage)
			CharacterLLM.mutex.unlock()
		}
	)
)