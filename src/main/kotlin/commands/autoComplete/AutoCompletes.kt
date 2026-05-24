package commands.autoComplete

import dev.kord.core.Kord
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph
import messages.webhooks.WebhookSender.sendAs

object AutoCompletes {
	private val commands: Map<String, AutoComplete> =
		ClassGraph()
			.enableClassInfo()
			.acceptPackages("commands.autoComplete.registry")
			.scan()
			.use { scanResult ->
				scanResult
					.getSubclasses(AutoComplete::class.qualifiedName)
					.loadClasses(AutoComplete::class.java)
					.mapNotNull { clazz -> clazz.kotlin.objectInstance }
					.associateBy { it.commandName }
			}

	fun registerAll(kord: Kord) {
		kord.on<AutoCompleteInteractionCreateEvent> {
			try {
				commands[interaction.command.rootName]!!.run(interaction)
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