package commands.registry

import commands.helpers.Exposer
import commands.helpers.Exposer.getExposeMessages
import commands.slashCommands.SlashCommand
import dev.kord.common.Color
import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.embed

object ExposeCommand : SlashCommand(
	name = "expose",
	description = "Expose a user's messages",
	args = {
		user("user", "The user to expose") {
			required = true
		}
	},
	run = commandRun@{ response ->
		if (command.users["user"]!!.getExposeMessages().isEmpty()) error("No saved messages for this user")

		response.respond {
			embed {
				color = Color(0xFF69B4)

				command.users["user"]!!.getExposeMessages().reversed().forEach {
					field {
						value = buildString {
							appendLine(it.message)
							append(it.timestamp.toMessageFormat(DiscordTimestampStyle.RelativeTime))
						}
					}
				}

				author {
					name = command.users["user"]!!.asMember(SlashCommand.guildID).effectiveName
					icon = command.users["user"]!!.avatar?.cdnUrl?.toUrl()
				}
			}
		}
	}
)