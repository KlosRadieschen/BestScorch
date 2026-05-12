package messages.webhooks

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import kotlinx.coroutines.flow.first

object WebhookSender {
	suspend inline fun sendAs(
		kord: Kord,
		name: String,
		profilePictureLink: String,
		message: String,
		channelID: Snowflake
	) {
		val channel = kord.getChannelOf<TextChannel>(channelID)
			?: kord.getChannelOf<ThreadChannel>(channelID)
			?: error("Channel $channelID not found (or unknown type)")

		val webhooks = channel.guild.webhooks
		val botWebhook = webhooks.first { it.creatorId == kord.selfId }

		botWebhook.edit { channelId = if (channel is ThreadChannel) channel.parentId else channel.id }

		when (channel) {
			is ThreadChannel -> botWebhook.execute(botWebhook.token!!, channel.id) {
				content = message
				username = name
				avatarUrl = profilePictureLink
			}
			is TextChannel -> botWebhook.execute(botWebhook.token!!) {
				content = message
				username = name
				avatarUrl = profilePictureLink
			}
		}
	}

	suspend inline fun sendAs(
		kord: Kord,
		name: String,
		profilePictureLink: String,
		message: String,
		channelID: Snowflake,
		replyMessage: Message
	) {
		val channel = kord.getChannelOf<TextChannel>(channelID)
			?: kord.getChannelOf<ThreadChannel>(channelID)
			?: error("Channel $channelID not found (or unknown type)")

		val webhooks = channel.guild.webhooks
		val botWebhook = webhooks.first { it.creatorId == kord.selfId }

		botWebhook.edit { channelId = if (channel is ThreadChannel) channel.parentId else channel.id }

		val message = buildString {
			appendLine("> [Replying to](https://discord.com/channels/@me/${replyMessage.channelId}/${replyMessage.id}): ${replyMessage.author?.mention}")

			val sanitizedMessage = if (replyMessage.content.contains("\n")) {
				replyMessage.content.takeWhile { it != '\n' } + "..."
			} else if (replyMessage.content.length > 100) {
				replyMessage.content.take(100) + "..."
			} else {
				replyMessage.content
			}

			appendLine("> $sanitizedMessage")
			append(message)
		}

		when (channel) {
			is ThreadChannel -> botWebhook.execute(botWebhook.token!!, channel.id) {
				content = message
				username = name
				avatarUrl = profilePictureLink
			}
			is TextChannel -> botWebhook.execute(botWebhook.token!!) {
				content = message
				username = name
				avatarUrl = profilePictureLink
			}
		}
	}
}
