package commands.helpers

import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv

object Execution {
	val executees: MutableSet<String> = mutableSetOf()

	suspend fun execute(user: User) {
		user.asMember(SlashCommand.guildID).addRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		executees.add(user.id.value.toString())
	}

	suspend fun revive(user: User) {
		user.asMember(SlashCommand.guildID).removeRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		executees.remove(user.id.value.toString())
	}

	suspend fun reviveAll(kord: Kord) {
		executees.forEach { executee ->
			val revivee = kord.getUser(Snowflake(executee))!!
			revivee.asMember(SlashCommand.guildID).removeRole(Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID")))
		}
		executees.clear()
	}

	fun isExecuted(userID: String) = executees.contains(userID)
	fun isAnyoneExecuted() = executees.isNotEmpty()
}