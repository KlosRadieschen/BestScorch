package commands.slashCommands

import Config
import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution.isExecuted
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph
import messages.webhooks.WebhookSender.sendAs

object SlashCommands {
	private val activeUsersIDs = mutableSetOf<Snowflake>()

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
			if (interaction.user.isExecuted() && !interaction.user.isAdminAbuser()) {
				interaction.respondEphemeral {
					content = "https://tenor.com/view/thunder-cross-split-attack-tcsa-you-fell-for-it-fool-gif-24290983"
				}
				return@on
			} else if (activeUsersIDs.contains(interaction.user.id)) {
				interaction.respondEphemeral {
					content = "You can only have one running command at a time"
				}
				return@on
			}

			activeUsersIDs.add(interaction.user.id)

			try {
				commands[interaction.data.data.name.value]!!.run(interaction)
			} catch (e: Exception) {
				e.printStackTrace()

				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					e.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					interaction.channelId
				)
			} finally {
				activeUsersIDs.remove(interaction.user.id)
			}
		}
	}

	suspend fun deleteOld(kord: Kord) {
		val commands = kord.getGuildApplicationCommands(Config.Snowflakes.ahaGuildID)
		commands.collect { command ->
			command.delete()
		}
	}
}