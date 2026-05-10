package messages.responders.registry

import ai.LLM
import dev.kord.core.behavior.reply
import messages.responders.Responder

object LLMResponder : Responder(
	check = { content.contains("Scorch") },
	execute = { reply { content = LLM.generateMessage(content.orEmpty()) } }
)