package commands.polls

import commands.slashCommands.SlashCommand
import dev.kord.common.Color
import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.common.toMessageFormat
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.embed
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

				var msg: Message? = channel.createEmbed {
					color = Color(0x00ff00)
					title = question

					field(buildString {
						for ((i, emoji) in emojis.withIndex())
							appendLine("$emoji: ${responses.values[i]}")
					})

					field("Expires " + Instant.fromEpochMilliseconds(System.currentTimeMillis()+duration.inWholeMilliseconds).toMessageFormat(DiscordTimestampStyle.RelativeTime))

					author {
						name = user.asMember(SlashCommand.guildID).effectiveName
						icon = user.avatar?.cdnUrl?.toUrl()
					}

					timestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
				}

				for (emoji in emojis) msg!!.addReaction(ReactionEmoji.Unicode(emoji))

				channel.startPublicThreadWithMessage(msg!!.id, "Discussion") {
					autoArchiveDuration = ArchiveDuration.Week
				}

				sleep(duration.inWholeMilliseconds)

				msg = channel.getMessageOrNull(msg.id)

				msg?.let {
					val reactionsByEmoji = msg.reactions.associateBy { it.emoji }

					for ((i, emoji) in emojis.withIndex()) {
						val reaction = reactionsByEmoji[ReactionEmoji.Unicode(emoji)]
						responses.votes[i] = (reaction?.count?.coerceAtLeast(1)?.minus(1)) ?: 0
					}

					val totalVotes = responses.votes.sum()

					msg.edit {
						embed {
							color = Color(0xff0000)
							title = question

							field(buildString {
								for ((i, emoji) in emojis.withIndex()) {
									val percentage = if (totalVotes > 0) {
										responses.votes[i] * 100.0 / totalVotes
									} else {
										0.0
									}

									appendLine("$emoji: ${responses.values[i]} (${responses.votes[i]} vote(s), ${percentage.toInt()}%)")
								}
							})

							field("Expired " + Instant.fromEpochMilliseconds(System.currentTimeMillis()).toMessageFormat(DiscordTimestampStyle.RelativeTime))

							author {
								name = user.asMember(SlashCommand.guildID).effectiveName
								icon = user.avatar?.cdnUrl?.toUrl()
							}

							timestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
						}
					}
				}
			}

			PollResponses.Discussion -> {
				val msg = channel.createEmbed {
					color = Color(0x00a9ff)
					title = question

					author {
						name = user.asMember(SlashCommand.guildID).effectiveName
						icon = user.avatar?.cdnUrl?.toUrl()
					}

					timestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
				}

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