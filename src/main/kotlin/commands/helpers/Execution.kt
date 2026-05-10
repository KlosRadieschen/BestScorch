package commands.helpers

import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv

object Execution {
	val executedRoleID = Snowflake(Dotenv.load().get("EXECUTED_ROLE_ID"))
	val executees: MutableSet<Snowflake> = mutableSetOf()

	suspend fun User.execute() {
		this.asMember(SlashCommand.guildID).addRole(executedRoleID)
		executees.add(this.id)
	}

	suspend fun User.revive() {
		this.asMember(SlashCommand.guildID).removeRole(executedRoleID)
		executees.remove(this.id)
	}

	suspend fun reviveAll(kord: Kord): Set<User> {
		executees.forEach { executee ->
			val revivee = kord.getUser(executee)!!
			revivee.asMember(SlashCommand.guildID).removeRole(executedRoleID)
		}

		val users = executees.map { kord.getUser(it)!! }.toSet()
		executees.clear()
		return users
	}

	fun User.isExecuted() = executees.contains(this.id)

	fun isAnyoneExecuted() = executees.isNotEmpty()
}