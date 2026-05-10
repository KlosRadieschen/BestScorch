package commands.helpers

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toSet

object Ranks {
	val ranks = listOf(
		Snowflake(1195136604373782658), // Rifleman
		Snowflake(1195137477497868458), // Lance Corporal
		Snowflake(1195708423229165578), // Corporal
		Snowflake(1195136491148550246), // Sergeant
		Snowflake(1195757362439528549), // Staff Sergeant
		Snowflake(1195758137563689070), // Sergeant First Class
		Snowflake(1195758241221722232), // Master Sergeant
		Snowflake(1195758308519325716), // Sergeant Major
		Snowflake(1250582641921757335), // 2nd Lieutenant
		Snowflake(1195137253408768040), // 1st Lieutenant
		Snowflake(1195136284478410926), // Captain
		Snowflake(1195137362259349504), // Major
		Snowflake(1469432285068529778), // Colonel
		Snowflake(1469432555886608574), // Commander
		Snowflake(1469432790620831817), // Vice Admiral
		Snowflake(1469432935395627113), // Lieutenant General
		Snowflake(1195135956471255140)  // General
	)

	suspend fun Member.promote(kord: Kord, steps: Int): String {
		val oldRank = findRank(this.roles.toSet())
		val newRank = moveRank(oldRank, steps)

		this.addRole(newRank)
		this.removeRole(oldRank.id)

		return kord
			.getGuild(guildId)
			.roles
			.first { it.id == newRank }
			.name
	}

	private fun moveRank(current: Role, steps: Int): Snowflake {
		val currentIndex = ranks.indexOf(current.id)
		val newIndex = (currentIndex + steps).coerceIn(0, ranks.lastIndex)
		return ranks[newIndex]
	}

	private fun findRank(values: Set<Role>): Role {
		val matches = values.filter { it.id in ranks }

		return when (matches.size) {
			0 -> throw IllegalArgumentException("No valid rank found")
			1 -> matches.single()
			else -> throw IllegalArgumentException("Multiple valid ranks found: ${matches.map { it.name }}")
		}
	}
}