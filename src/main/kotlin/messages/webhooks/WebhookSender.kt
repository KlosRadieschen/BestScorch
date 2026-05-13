package messages.webhooks

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Message
import dev.kord.core.entity.Webhook
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import kotlinx.coroutines.flow.first

object WebhookSender {
	const val REPLY_MAX_CHARS = 150

	suspend fun sendAs(
		kord: Kord,
		name: String,
		profilePictureLink: String,
		message: String,
		channelID: Snowflake
	): Message {
		val channel = getChannel(kord, channelID)
		val botWebhook = getWebhookAndSetChannel(kord, channel)

		return executeInThreadOrChannel(botWebhook, channel, name, profilePictureLink, message)
	}

	suspend fun sendAs(
		kord: Kord,
		name: String,
		profilePictureLink: String,
		message: String,
		channelID: Snowflake,
		replyMessage: Message
	): Message {
		val channel = getChannel(kord, channelID)
		val botWebhook = getWebhookAndSetChannel(kord, channel)

		val message = buildString {
			val authorRef = replyMessage.author?.mention ?: replyMessage.data.author.username
			appendLine("> [Replying to](https://discord.com/channels/@me/${replyMessage.channelId}/${replyMessage.id}): $authorRef")

			val sanitizedMessage = replyMessage.content
				.split("\n")
				.filter { it.isNotEmpty() && !it.startsWith(">") }
				.joinToString("\n")
				.replace("\n", "   ")
				.trim()
				.take(REPLY_MAX_CHARS)
				.appendIf("...") { replyMessage.content.length > REPLY_MAX_CHARS }

			appendLine("> $sanitizedMessage")
			append(message)
		}

		return executeInThreadOrChannel(botWebhook, channel, name, profilePictureLink, message)
	}

	private suspend inline fun getChannel(kord: Kord, channelID: Snowflake): GuildMessageChannel = kord.getChannelOf<TextChannel>(channelID)
		?: kord.getChannelOf<ThreadChannel>(channelID)
		?: error("Channel $channelID not found (or unknown type)")

	private suspend inline fun getWebhookAndSetChannel(kord: Kord, channel: GuildMessageChannel): Webhook {
		val webhooks = channel.guild.webhooks
		val botWebhook = webhooks.first { it.creatorId == kord.selfId }

		botWebhook.edit { channelId = if (channel is ThreadChannel) channel.parentId else channel.id }

		return botWebhook
	}

	private suspend inline fun executeInThreadOrChannel(botWebhook: Webhook, channel: GuildMessageChannel, name: String, profilePictureLink: String, message: String): Message {
		return when (channel) {
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

            else -> error("Channel $channel not supported")
        }
	}

	private fun String.appendIf(appendString: String, condition: (String) -> Boolean): String =
		if (condition(this)) "$this$appendString" else this
}
