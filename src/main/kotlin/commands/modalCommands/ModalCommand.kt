package commands.modalCommands

import dev.kord.core.entity.interaction.ModalSubmitInteraction

abstract class ModalCommand(
	val id: String,
	val run: suspend ModalSubmitInteraction.() -> Unit
)