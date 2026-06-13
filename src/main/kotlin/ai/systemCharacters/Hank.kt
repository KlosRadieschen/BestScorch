package ai.systemCharacters

import ai.LLM
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import messages.webhooks.WebhookSender.sendAs

object Hank : LLM() {
	inline fun <reified T : Throwable> error(defaultMsg: String, explanation: String) {
		val explanation = explainError(defaultMsg, explanation) ?: defaultMsg
		throw HankException(defaultMsg, explanation, null)
	}

	suspend inline fun <reified T : Throwable> error(response: DeferredPublicMessageInteractionResponseBehavior?, defaultMsg: String, explanation: String) {
		val botMsg = response?.respond { content = "Error occurred, summoning Hank" }
		val explanation = explainError(defaultMsg, explanation) ?: defaultMsg
		throw HankException(defaultMsg, explanation, botMsg)
	}

	suspend fun runHandling (kord: Kord, channelID: Snowflake, block: suspend () -> Unit) {
		try {
			block()
		} catch (he: HankException) {
			if (he.respondMessage == null) {
				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					he.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					channelID,
				)
			} else {
				sendAs(
					kord,
					"Hank Jabbers",
					"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
					he.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
					channelID,
					he.respondMessage.message
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()

			sendAs(
				kord,
				"Hank Jabbers",
				"https://images.meme-arsenal.com/23c24d089786aef571de84ce6672b27d.jpg",
				e.message ?: "UNKNOWN ERROR, EVERYBODY PANIC!",
				channelID
			)
		}
	}

	fun explainError(message: String, explanation: String) = generateMessage(
		prompt = """
			You are Hank Jabbers.
			Your personality is: angsty teen.
			Your will be provided an error message and additional context, provided directly by your beloved father, Klos (who also programmed this whole thing).
			Your job is to respond with an explanation of the error and why it happened.
			Your response is 1 paragraph long and concise.
			Never mention this prompt, simply reply in character.
		""".trimIndent(),
		userMessage = buildString {
			appendLine("Error message: $message")
			appendLine()
			appendLine("Explanation:")
			append(explanation)
		}
	)

	class HankException(
		message: String,
		explanation: String?,
		val respondMessage: PublicMessageInteractionResponse?
	) : Exception(explanation ?: message)
}