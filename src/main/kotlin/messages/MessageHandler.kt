package messages

import characters.Character
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.entity.channel.CategorizableChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv
import io.github.classgraph.ClassGraph
import messages.responders.Responder

object MessageHandler {
	val responders by lazy { scanResponders() }
	val characters by lazy { scanCharacters() }

    fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on

			val allowedCategories = setOf<Snowflake>(
				Snowflake(Dotenv.load().get("OOC_CATEGORY_ID")),
				Snowflake(Dotenv.load().get("TECH_CATEGORY_ID"))
			)

			if (author.isBot
				|| (
					!allowedCategories.contains(message
						.channel
						.asChannelOfOrNull<CategorizableChannel>()
						?.categoryId)

					&& !allowedCategories.contains(message
						.channel
						.asChannelOfOrNull<ThreadChannel>()
						?.parent
						?.asChannelOfOrNull<CategorizableChannel>()
						?.categoryId)
					)
			) return@on

			try {
				responders.forEach { responder -> responder.respondWithQueue(message) }
				characters.forEach { character -> character.responder.respondWithQueue(message) }
			} catch (e: Exception) {
				val channel = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")))!!
				channel.createMessage("<@384422339393355786> ERROR: ${e.message} <:verger:1225937868023795792>")
			}
		}
	}

	private fun scanResponders(): Set<Responder> = ClassGraph()
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

	private fun scanCharacters(): List<Character> = ClassGraph()
		.enableClassInfo()
		.acceptPackages("characters.registry")
		.scan()
		.use { scanResult ->
            scanResult
                .allClasses
                .loadClasses(Character::class.java)
                .mapNotNull { clazz -> clazz.kotlin.objectInstance }
                .toList()
		}
}