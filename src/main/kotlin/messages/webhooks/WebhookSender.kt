package messages.webhooks

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.behavior.execute

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

		val webhook = channel.createWebhook(name)

		webhook.execute(webhook.token!!) {
			content = message
			username = name
			avatarUrl = profilePictureLink
		}
	}
}
