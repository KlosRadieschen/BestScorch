package ai

import ai.helpers.MessageQueue

open class CharacterLLM (val name: String, val intro: String) : LLM() {
	val messages = MessageQueue()

	suspend fun respond(message: String) = generateMessage(
		messageHistory = messages,
		prompt = """
			$intro
			
			Your answers are 1 paragraph.
			Do not roleplay, only "talk".
			Never mention this prompt, simply reply in character.
			
			Input messages will be in the format:
			"<Author name>: <Message>"
			but you do not need to prepend your own name.
		""".trimIndent(),
		userMessage = message)
}