package commands.slashCommands

import commands.helpers.Execution.isExecuted
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph
import kotlinx.coroutines.sync.Mutex
import messages.webhooks.WebhookSender.sendAs

object SlashCommands {
	private val mutex = Mutex()

	private val commands: Map<String, SlashCommand> =
		ClassGraph()
			.enableClassInfo()
			.acceptPackages("commands.slashCommands.registry")
			.scan()
			.use { scanResult ->
				scanResult
					.getSubclasses(SlashCommand::class.qualifiedName)
					.loadClasses(SlashCommand::class.java)
					.mapNotNull { clazz -> clazz.kotlin.objectInstance }
					.associateBy { it.name }
			}

	suspend fun createAll(kord: Kord) = commands.values.forEach { it.create(kord) }

	fun registerAll(kord: Kord) {
		kord.on<GuildChatInputCommandInteractionCreateEvent> {
			if (interaction.user.isExecuted()) {
				interaction.respondEphemeral {
					content = "https://tenor.com/view/thunder-cross-split-attack-tcsa-you-fell-for-it-fool-gif-24290983"
				}
				return@on
			}

			mutex.lock()
			val response = interaction.deferPublicResponse()

			try {
				commands[interaction.data.data.name.value]!!.run(interaction, response)
			} catch (e: Exception) {
				e.printStackTrace()

				val msg = response.respond { content = "Error occurred, summoning Hank" }
				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					e.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					interaction.channelId,
					msg.message
				)
			} finally {
			    mutex.unlock()
			}
		}
	}

	suspend fun deleteOld(kord: Kord) {
		val commands = kord.getGuildApplicationCommands(SlashCommand.guildID)
		commands.collect { command ->
			command.delete()
		}
	}
}