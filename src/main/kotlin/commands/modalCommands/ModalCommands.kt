package commands.modalCommands

import ai.systemCharacters.Hank
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph

object ModalCommands {
	private val commands: Map<String, ModalCommand> =
		ClassGraph()
			.enableClassInfo()
			.acceptPackages("commands.modalCommands.registry")
			.scan()
			.use { scanResult ->
				scanResult
					.getSubclasses(ModalCommand::class.qualifiedName)
					.loadClasses(ModalCommand::class.java)
					.mapNotNull { clazz -> clazz.kotlin.objectInstance }
					.associateBy { it.id }
			}

	fun registerAll(kord: Kord) {
		kord.on<ModalSubmitInteractionCreateEvent> {
			val response = interaction.deferEphemeralResponse()

			Hank.runHandling(kord, interaction.channelId) {
				commands[interaction.modalId.split(":")[0]]!!.run(interaction)
				response.respond { content = "Modal submitted" }
			}
		}
	}
}