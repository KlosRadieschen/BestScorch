package commands.helpers

import dev.kord.common.entity.Snowflake
import io.github.cdimascio.dotenv.Dotenv

object AdminAbusers {
	val adminAbuserIds = Dotenv.load().get("ADMIN_ABUSERS")!!.split(",").toSet()

	fun isAdminAbuser(userId: Snowflake): Boolean = adminAbuserIds.contains(userId.value.toString())
}
