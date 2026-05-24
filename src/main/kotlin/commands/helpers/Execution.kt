package commands.helpers

import Config
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User

object Execution {
	val executedRoleID = Config.Snowflakes.executedRoleID
	val executees: MutableSet<Snowflake> = mutableSetOf()

	suspend fun User.execute() {
		this.asMember(Config.Snowflakes.ahaGuildID).addRole(executedRoleID)
		executees.add(this.id)
	}

	suspend fun User.revive() {
		this.asMember(Config.Snowflakes.ahaGuildID).removeRole(executedRoleID)
		executees.remove(this.id)
	}

	suspend fun reviveAll(kord: Kord): Set<User> {
		executees.forEach { executee ->
			val revivee = kord.getUser(executee)!!
			revivee.asMember(Config.Snowflakes.ahaGuildID).removeRole(executedRoleID)
		}

		val users = executees.map { kord.getUser(it)!! }.toSet()
		executees.clear()
		return users
	}

	fun User.isExecuted() = executees.contains(this.id)

	fun isAnyoneExecuted() = executees.isNotEmpty()
}