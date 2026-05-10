package messages

import commands.helpers.ExposeMessage
import commands.helpers.Exposer.addExposeMessage
import dev.kord.core.entity.Message
import dev.kord.core.entity.User

object ExposerHandler {
	fun handleExpose(user: User, message: Message) {
		user.addExposeMessage(ExposeMessage(message.content, message.timestamp))
	}
}