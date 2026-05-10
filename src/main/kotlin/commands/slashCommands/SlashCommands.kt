package commands.slashCommands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph

class SlashCommands {
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
			val response = interaction.deferPublicResponse()
			try {
				commands[interaction.data.data.name.value]!!.run(interaction, response)
			} catch (e: Exception) {
				e.printStackTrace()
				response.respond { content = "ERROR: ${e.message} <:verger:1225937868023795792>" }
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