package messages

import commands.helpers.Execution.isExecuted
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv
import io.github.classgraph.ClassGraph
import messages.responders.Responder

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on
			if (author.isBot) return@on

			val responders = ClassGraph()
				.enableClassInfo()
				.acceptPackages("messages.responders.registry")
				.scan()
				.use { scanResult ->
					scanResult
						.getSubclasses(Responder::class.qualifiedName)
						.loadClasses(Responder::class.java)
						.mapNotNull { clazz -> clazz.kotlin.objectInstance }
				}


			try {
				if (author.isExecuted()) message.delete()
				else responders.forEach { responder -> responder.respond(message) }
			} catch (e: Exception) {
				val channel = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")))!!
				channel.createMessage("<@384422339393355786> ERROR: ${e.message} <:verger:1225937868023795792>")
			}
		}
	}
}