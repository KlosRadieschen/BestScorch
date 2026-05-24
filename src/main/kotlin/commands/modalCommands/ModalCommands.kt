package commands.modalCommands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph
import messages.webhooks.WebhookSender.sendAs

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

			try {
				commands[interaction.modalId.split(":")[0]]!!.run(interaction)
				response.respond { content = "Modal submitted" }
			} catch (e: Exception) {
				e.printStackTrace()

				response.respond { content = "Error, summoning Hank" }
				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					e.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					interaction.channelId,
				)
			}
		}
	}
}