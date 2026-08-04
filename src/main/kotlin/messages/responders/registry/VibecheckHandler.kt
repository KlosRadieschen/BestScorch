package messages.responders.registry

import commands.helpers.Vibechecker
import commands.helpers.Vibechecker.checkVibe
import commands.helpers.Vibechecker.isVibeAwaited
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.reply
import kotlinx.coroutines.delay
import messages.responders.Responder
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

object VibecheckHandler : Responder(
	check = { author?.isVibeAwaited() ?: false },
	execute = {
		val m = reply {files += Vibechecker.investigationImages.random()}
		delay(Random.nextInt(1, 10).seconds)
		m.edit {files += author!!.checkVibe()}
	}
)