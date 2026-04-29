package commands.polls

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.common.toMessageFormat
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.edit
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv
import java.lang.Thread.sleep
import kotlin.time.Duration
import kotlin.time.Instant

class Poll (
	val question: String,
	val responses: PollResponses,
	val duration: Duration
) {
	companion object {
		val pollChannelID = Dotenv.load().get("POLL_CHANNEL_ID")!!
	}

	suspend fun start(kord: Kord, user: User) {
		val channel = kord.getChannel(Snowflake(pollChannelID))!! as TextChannelBehavior

		when (responses) {
			is PollResponses.Options -> {
				val emojis = listOf("🔥","🍷","💀","👻","🎶","💦","🫠","☕","🕊","💜").take(responses.votes.size)

				var msg = channel.createMessage(buildString {
					append("# $question\n(Poll by ${user.mention})\n")
					append("Poll expires ${Instant.fromEpochMilliseconds(System.currentTimeMillis()+duration.inWholeMilliseconds).toMessageFormat(DiscordTimestampStyle.RelativeTime)}")
					for ((i, emoji) in emojis.withIndex()) {
						append("\n$emoji: ")
						append(responses.values[i])
					}
				})

				DiscordTimestampStyle.RelativeTime
				for (emoji in emojis) msg.addReaction(ReactionEmoji.Unicode(emoji))

				channel.startPublicThreadWithMessage(msg.id, "Discussion") {
					autoArchiveDuration = ArchiveDuration.Week
				}

				sleep(duration.inWholeMilliseconds)

				msg = channel.getMessage(msg.id)
				for ((i, reaction) in msg.reactions.take(responses.votes.size).withIndex()) {
					reaction.emoji
					responses.votes[i] = reaction.count-1
				}

				val totalVotes = responses.votes.sum()

				msg.edit {
					content = buildString {
						append("# $question\n(Poll by ${user.mention})\n")
						append("Poll expired ${Instant.fromEpochMilliseconds(System.currentTimeMillis()).toMessageFormat(DiscordTimestampStyle.RelativeTime)}")
						for ((i, emoji) in emojis.withIndex()) {
							val percentage = if (totalVotes > 0) {
								responses.votes[i] * 100.0 / totalVotes
							} else {
								0.0
							}

							append("\n$emoji ")
							append("(${responses.votes[i]} ")
							if (responses.votes[i] == 1) append("vote") else append("votes")
							append(", ${percentage.toInt()}%): ")
							append(responses.values[i])
						}
					}
				}
			}

			PollResponses.Discussion -> {
				val msg = channel.createMessage("# $question\n(Discussion by ${user.mention})")
				channel.startPublicThreadWithMessage(msg.id, "DISCUSS") {
					autoArchiveDuration = ArchiveDuration.Week
				}
			}

			is PollResponses.Inputs -> {
				TODO("COMING SOON")
			}
		}
	}
}