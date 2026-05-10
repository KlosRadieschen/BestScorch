package commands.slashCommands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Ranks.promote
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user

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
	run = { response ->
		val targetMember = kord.getUser(command.users["user"]!!.id)!!.withStrategy(EntitySupplyStrategy.rest).asMember(guildID)
		if (!user.isAdminAbuser()) error("You are not an admin abuser")

		val steps = (command.integers["steps"] ?: 1).toInt()

		val newRank = targetMember.promote(kord, steps)

		val content = buildString {
			append("${targetMember.mention} has been ")
			if (steps > 0) append("promoted to ") else append("demoted to ")
			appendLine("$newRank:")
			if (!command.strings["reason"].isNullOrBlank()) append(command.strings["reason"])
		}

		response.respond { this.content = content }
	}
)