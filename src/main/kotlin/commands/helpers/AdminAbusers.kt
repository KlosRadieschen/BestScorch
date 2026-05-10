package commands.helpers

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv

enum class ImmunityStatus {
	Immune,
	NotImmune,
}

data class AdminAbuser(val userId: Snowflake, val status: ImmunityStatus)

object AdminAbusers {
	val adminAbusers: Set<AdminAbuser> = listOf(
		"ADMIN_ABUSERS_NOT_IMMUNE" to ImmunityStatus.NotImmune,
		"ADMIN_ABUSERS_IMMUNE" to ImmunityStatus.Immune
	).flatMap { (key, status) ->
		Dotenv
			.load()
			.get(key)
			?.split(",")
			?.map { AdminAbuser(Snowflake(it), status) }
			?: emptyList()
	}.toSet()

	fun User.isAdminAbuser(): Boolean = adminAbusers.any { it.userId == id }
	fun User.isImmune(): Boolean = adminAbusers.any { it.userId == id && it.status == ImmunityStatus.Immune }
}
