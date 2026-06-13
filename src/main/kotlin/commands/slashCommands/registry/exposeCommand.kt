package commands.slashCommands.registry

import Config
import ai.systemCharacters.Hank
import commands.helpers.Exposer.exposeMessages
import commands.slashCommands.SlashCommand
import dev.kord.common.Color
import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.embed

@Suppress("UNUSED")
object ExposeCommand : SlashCommand(
	name = "expose",
	description = "Expose a user's messages",
	args = {
		user("user", "The user to expose") {
			required = true
		}
	},
	run = {
		val response = deferPublicResponse()

		try {
			response.respond {
				embed {
					color = Color(0xFF69B4)

					command.users["user"]!!.exposeMessages.reversed().forEach {
						field {
							value = buildString {
								appendLine(it.message)
								append(it.timestamp.toMessageFormat(DiscordTimestampStyle.RelativeTime))
							}
						}
					}

					author {
						name = command.users["user"]!!.asMember(Config.Snowflakes.ahaGuildID).effectiveName
						icon = command.users["user"]!!.avatar?.cdnUrl?.toUrl()
					}
				}
			}
		} catch (e: Exception) {
			Hank.error<IllegalStateException>(
				response,
				e.message ?: e.toString(),
				"""
				We are in the command "expose" which lets you expose the last 5 messages of a user, even if they are edited or deleted.
				However, ${user.asMember(Config.Snowflakes.ahaGuildID).effectiveName} tried using it on someone who hasn't posted any messages since the last restart of this bot.
			""".trimIndent())
		}

		response
	}
)