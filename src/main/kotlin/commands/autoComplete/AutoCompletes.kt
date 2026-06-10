package commands.autoComplete

import ai.systemCharacters.Hank
import dev.kord.core.Kord
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.on
import io.github.classgraph.ClassGraph

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
			Hank.runHandling(kord, interaction.channelId) {
				commands[interaction.command.rootName]!!.run(interaction)
			}
		}
	}
}