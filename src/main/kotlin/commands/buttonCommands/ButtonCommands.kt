package commands.buttonCommands

import ai.systemCharacters.Hank
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph

object ButtonCommands {
	private val commands: Map<String, ButtonCommand> =
		ClassGraph()
			.enableClassInfo()
			.acceptPackages("commands.buttonCommands.registry")
			.scan()
			.use { scanResult ->
				scanResult
					.getSubclasses(ButtonCommand::class.qualifiedName)
					.loadClasses(ButtonCommand::class.java)
					.mapNotNull { clazz -> clazz.kotlin.objectInstance }
					.associateBy { it.id }
			}

	fun registerAll(kord: Kord) {
		kord.on<ButtonInteractionCreateEvent> {
			Hank.runHandling(kord, interaction.channelId) {
				commands[interaction.componentId.split(":")[0]]!!.run(interaction)
			}
		}
	}
}