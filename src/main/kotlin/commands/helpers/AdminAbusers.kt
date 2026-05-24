package commands.helpers

import Config
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User

object AdminAbusers {
	enum class ImmunityStatus {
		Immune,
		NotImmune,
	}

	data class AdminAbuser(val userId: Snowflake, val status: ImmunityStatus)

	val adminAbusers: Set<AdminAbuser> = listOf(
		"ADMIN_ABUSERS_NOT_IMMUNE" to Config.Snowflakes.AdminAbusers.adminAbusersNotImmune,
		"ADMIN_ABUSERS_IMMUNE" to Config.Snowflakes.AdminAbusers.adminAbusersImmune,
	).flatMap { (key, snowflakes) ->
		snowflakes.map { userId ->
			AdminAbuser(
				userId = userId,
				status = if (key == "ADMIN_ABUSERS_NOT_IMMUNE") ImmunityStatus.NotImmune else ImmunityStatus.Immune
			)
		}
	}.toSet()

	fun User.isAdminAbuser(): Boolean = adminAbusers.any { it.userId == id }
	fun User.isImmune(): Boolean = adminAbusers.any { it.userId == id && it.status == ImmunityStatus.Immune }
}
