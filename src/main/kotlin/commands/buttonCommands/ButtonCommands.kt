package commands.buttonCommands

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph
import messages.webhooks.WebhookSender.sendAs

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
			try {
				commands[interaction.componentId.split(":")[0]]!!.run(interaction)
			} catch (e: Exception) {
				e.printStackTrace()

				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					e.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					interaction.channelId
				)
			}
		}
	}
}