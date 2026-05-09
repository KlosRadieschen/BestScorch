package messages.webhooks

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.execute
import kotlinx.coroutines.flow.first

object WebhookSender {
	suspend fun sendAs(
		kord: Kord,
		name: String,
		profilePictureLink: String,
		message: String,
		channelID: Snowflake
	) {
		val channel = kord.getChannelOf<dev.kord.core.entity.channel.TextChannel>(channelID)
			?: error("Channel $channelID not found")

		val webhooks = channel.guild.webhooks
		val botWebhook = webhooks.first { it.creatorId == kord.selfId }

		botWebhook.edit { channelId = channel.id }
		botWebhook.execute(botWebhook.token!!) {
			content = message
			username = name
			avatarUrl = profilePictureLink
		}
	}
}
