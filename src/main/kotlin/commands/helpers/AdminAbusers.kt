package commands.helpers

import io.github.cdimascio.dotenv.Dotenv

object AdminAbusers {
   val adminAbuserIds = Dotenv.load().get("ADMIN_ABUSERS")!!.split(",").toSet()

    fun isAdminAbuser(userId: ULong): Boolean = adminAbuserIds.contains(userId.toString())
}
