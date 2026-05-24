package commands.buttonCommands

import dev.kord.core.entity.interaction.ButtonInteraction

abstract class ButtonCommand(
	val id: String,
	val run: suspend ButtonInteraction.() -> Unit
)