package commands.slashCommands.registry

import commands.helpers.Arena
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.interaction.user

@Suppress("UNUSED")
object ChallengeCommand : SlashCommand (
	name = "challenge",
	description = "Challenge someone to a gambling competition",
	args = {
		user("opponent", "Your opponent") {
			required = true
		}
	},
	run = {
		val opponent = command.users["opponent"]!!
		val uuid = Arena.startFight(user, opponent)

		respondPublic {
			content = "${opponent.mention}, ${user.mention} CHALLENGES YOU TO A GAMBLING DUEL"
			actionRow {
				interactionButton(ButtonStyle.Success, "challenge:acc~$uuid") {
					label = "Accept"
				}
				interactionButton(ButtonStyle.Danger, "challenge:dec~$uuid") {
					label = "Decline"
				}
			}
		}

		null
	}
)