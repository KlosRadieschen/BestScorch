package commands.helpers

import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.User
import java.util.*

object Arena {
	data class Fighter(
		val user: User,
		var result: Int?,
	)

	data class Fight(
		val challenger: Fighter,
		val challengee: Fighter,
		var message: PublicMessageInteractionResponseBehavior?,
	)

	val fights = mutableMapOf<String, Fight>()

	fun startFight(challenger: User, challengee: User): String {
		val id = UUID.randomUUID().toString()
		fights[id] = Fight(Fighter(challenger, null), Fighter(challengee, null), null)
		return id
	}

	fun stopFight(id: String) {
		fights.remove(id)
	}
}