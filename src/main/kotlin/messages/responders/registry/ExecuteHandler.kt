package messages.responders.registry

import commands.helpers.Execution.isExecuted
import messages.responders.Responder

object ExecuteHandler : Responder(
	check = { author?.isExecuted() ?: false },
	execute = { delete() }
)
