package characters

import messages.responders.Responder

abstract class Character (
	open val name: String,
	open val pfp: String,
	val responder: Responder
)