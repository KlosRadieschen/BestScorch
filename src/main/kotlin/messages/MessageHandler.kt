package messages

import dev.kord.common.entity.GuildBadgeType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.entity.channel.CategorizableChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv
import io.github.classgraph.ClassGraph
import io.ktor.util.reflect.instanceOf
import messages.responders.Responder

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on

			val allowedCategories = setOf<Snowflake>(
				Snowflake(Dotenv.load().get("OOC_CATEGORY_ID")),
				Snowflake(Dotenv.load().get("TECH_CATEGORY_ID"))
			)

			if (author.isBot || !allowedCategories.contains(message.channel.asChannelOfOrNull<CategorizableChannel>()?.categoryId)) return@on

			val responders = ClassGraph()
				.enableClassInfo()
				.acceptPackages("messages.responders.registry")
				.scan()
				.use { scanResult ->
					scanResult
						.getSubclasses(Responder::class.qualifiedName)
						.loadClasses(Responder::class.java)
						.mapNotNull { clazz -> clazz.kotlin.objectInstance }
						.toSet()
				}

			val characters = ClassGraph()
				.enableClassInfo()
				.acceptPackages("characters.registry")
				.scan()
				.use { scanResult ->
					scanResult
						.allClasses
						.loadClasses(characters.Character::class.java)
						.mapNotNull { clazz -> clazz.kotlin.objectInstance }
						.toSet()
				}

			try {
				responders.forEach { responder -> responder.respond(message) }
				characters.forEach { character -> character.responder.respond(message) }
			} catch (e: Exception) {
				val channel = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")))!!
				channel.createMessage("<@384422339393355786> ERROR: ${e.message} <:verger:1225937868023795792>")
			}
		}
	}
}