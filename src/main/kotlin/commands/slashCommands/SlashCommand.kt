package commands.slashCommands

import Config
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder


abstract class SlashCommand(
	val name: String,
	val description: String,
	val args: ChatInputCreateBuilder.() -> Unit,
	val run: suspend GuildChatInputCommandInteraction.() -> DeferredPublicMessageInteractionResponseBehavior?,
) {
	suspend fun create(kord: Kord) {
		kord.createGuildChatInputCommand(
			Config.Snowflakes.ahaGuildID,
			name,
			description,
			args
		)
	}
}