package messages.responders.registry

import commands.helpers.ExposeMessage
import commands.helpers.Exposer.addExposeMessage
import messages.responders.Responder

object ExposerHandler : Responder(
	check = { true },
	execute = { author?.addExposeMessage(ExposeMessage(content.take(1000), timestamp)) }
)