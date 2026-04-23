package commands.helpers

import dev.kord.core.entity.Role

object Ranks {
	val ranks = listOf(
		"1195136604373782658", // Rifleman
		"1195137477497868458", // Lance Corporal
		"1195708423229165578", // Corporal
		"1195136491148550246", // Sergeant
		"1195757362439528549", // Staff Sergeant
		"1195758137563689070", // Sergeant First Class
		"1195758241221722232", // Master Sergeant
		"1195758308519325716", // Sergeant Major
		"1250582641921757335", // 2nd Lieutenant
		"1195137253408768040", // 1st Lieutenant
		"1195136284478410926", // Captain
		"1195137362259349504", // Major
		"1469432285068529778", // Colonel
		"1469432555886608574", // Commander
		"1469432790620831817", // Vice Admiral
		"1469432935395627113", // Lieutenant General
		"1195135956471255140"  // General
	)

	fun moveRank(current: Role, steps: Int): String {
		val currentIndex = ranks.indexOf(current.id.value.toString())
		val newIndex = (currentIndex + steps).coerceIn(0, ranks.lastIndex)
		return ranks[newIndex]
	}

	fun findRank(values: Set<Role>): Role {
		val matches = values.filter { it.id.value.toString() in ranks }

		return when (matches.size) {
			0 -> throw IllegalArgumentException("No valid rank found")
			1 -> matches.single()
			else -> throw IllegalArgumentException("Multiple valid ranks found: $matches")
		}
	}
}