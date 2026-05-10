package commands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Ranks
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import kotlinx.coroutines.flow.toSet

object PromoteCommand : SlashCommand(
	name = "promote",
	description = "Promote or demote a user",
	args = {
		user("user", "The user to promote/demote") {
			required = true
		}
		integer("steps", "The amount of ranks to go up/down (Default: 1)")
		string("reason", "Reason for the promotion/demotion")
	},
	run = commandRun@{ response ->
		val targetMember = kord.getUser(command.users["user"]!!.id)!!.asMember(SlashCommand.guildID)
		if (!user.isAdminAbuser()) {
			response.respond { content = "You are not an admin abuser" }
			return@commandRun
		}

		val steps = (command.integers["steps"] ?: 1).toInt()

		val oldRank = Ranks.findRank(targetMember.roles.toSet())
		val newRank = Ranks.moveRank(oldRank, steps)
		targetMember.removeRole(oldRank.id)
		targetMember.addRole(Snowflake(newRank))

		val content = buildString {
			append("${targetMember.mention} has been ")
			if (steps > 0) append("promoted:\n") else append("demoted:\n")
			if (!command.strings["reason"].isNullOrBlank()) append(command.strings["reason"])
		}

		response.respond { this.content = content }
	}
)