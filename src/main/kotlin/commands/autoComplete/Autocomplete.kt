package commands.autoComplete

import dev.kord.core.entity.interaction.AutoCompleteInteraction

abstract class AutoComplete(
	val commandName: String,
	val run: suspend AutoCompleteInteraction.() -> Unit
)