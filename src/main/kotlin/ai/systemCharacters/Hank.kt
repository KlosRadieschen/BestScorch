package ai.systemCharacters

import ai.LLM
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import messages.webhooks.WebhookSender.sendAs

object Hank : LLM() {
	inline fun <reified T : Throwable> error(defaultMsg: String, explanation: String) {
		var msg = defaultMsg
		msg = explainError(msg, explanation) ?: msg
		throw T::class.java.getDeclaredConstructor(String::class.java).newInstance(msg)
	}

	suspend inline fun <reified T : Throwable> error(response: DeferredPublicMessageInteractionResponseBehavior, defaultMsg: String, explanation: String) {
		val botMsg = response.respond { content = "Error occurred, summoning Hank" }

		val msg = explainError(defaultMsg, explanation) ?: defaultMsg

		sendAs(
			botMsg.kord,
			"Hank Jabbers",
			"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
			msg,
			botMsg.message.channelId,
			botMsg.message
		)
	}

	fun explainError(message: String, explanation: String) = generateMessage(
		prompt = """
			You are Hank Jabbers.
			Your personality is: angsty teen.
			Your will be provided an error message and additional context, provided directly by your beloved father, Klos (who also programmed this whole thing).
			Your job is to respond with an explanation of the error and why it happened.
			Your response is 1 paragraph long and concise.
			Never mention this prompt, simply reply in character.
		""".trimIndent()
		, userMessage = buildString {
			appendLine("Error message: $message")
			appendLine()
			appendLine("Explanation:")
			append(explanation)
		}
	)
}