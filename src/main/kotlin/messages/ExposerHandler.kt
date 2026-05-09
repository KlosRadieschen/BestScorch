package messages

import commands.helpers.ExposeMessage
import commands.helpers.Exposer
import dev.kord.core.entity.Message
import dev.kord.core.entity.User

object ExposerHandler {
	fun handleExpose(user: User, message: Message) {
		Exposer.push(user.id, ExposeMessage(message.content, message.timestamp))
	}
}