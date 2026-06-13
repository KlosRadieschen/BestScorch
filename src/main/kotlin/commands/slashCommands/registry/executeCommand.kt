package commands.slashCommands.registry

import commands.helpers.AdminAbusers.isAdminAbuser
import commands.helpers.Execution.execute
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.rest.builder.interaction.user

@Suppress(names = ["unused"])
object ExecuteCommand : SlashCommand(
	name = "execute",
	description = "MURDER",
	args = {
		user("user", "The user to MURDER") {
			required = true
		}
	},
	run = {
		val target = kord.getUser(Snowflake(command.users["user"]!!.id.value))!!
		val executee = if (user.isAdminAbuser()) target else user

		executee.execute()

		respondPublic { content = "${executee.mention} was MURDERED" }

		null
	}
)