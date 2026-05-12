package characters

import ai.CharacterLLM
import dev.kord.core.entity.effectiveName
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
		check = { content.lowercase().contains(Regex("\\b${name.lowercase()}\\b")) || referencedMessage?.data?.author?.username == name },
		execute = {
			sendAs(
				kord,
				name = name,
				profilePictureLink = pfp,
				message = llm.respond(content).orEmpty(),
				channelID = channelId,
				this
			)
		}
	)
)