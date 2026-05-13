package characters

import dev.kord.core.behavior.reply
import messages.responders.Responder
import messages.webhooks.WebhookSender.sendAs

abstract class TextCharacter(
	override val name: String,
	override val pfp: String,
	val response: String
) : Character (
	name,
	pfp,
	Responder(
		check = { content.lowercase().contains(Regex("(?<!\\\\)\\b${name.lowercase()}\\b")) },
		execute =  {
			sendAs(
				kord,
				name = name,
				profilePictureLink = pfp,
				message = response,
				channelID = channelId,
				this
			)
		}
	)
) {
}