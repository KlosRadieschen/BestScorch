package dev.kord.core.commands.slashCommands

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder


class SlashCommand(
	val name: String,
	val description: String,
	val args: ChatInputCreateBuilder.() -> Unit,
	val run: suspend GuildChatInputCommandInteraction.(DeferredPublicMessageInteractionResponseBehavior) -> Unit,
) {
	companion object {
		val guildID = Snowflake(1195135473006420048)
	}

	suspend fun create(kord: Kord) {
		kord.createGuildChatInputCommand(
			guildID,
			name,
			description,
			args
		)
	}
}