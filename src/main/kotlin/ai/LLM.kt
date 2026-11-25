package ai

import Config
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.ReasoningEffort
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionMessage

abstract class LLM {
	companion object {
		val token = Config.Tokens.mistralToken
		const val BASE_URL = "https://api.mistral.ai/v1/"
		const val MODEL_ID = "mistral-large-latest"

		fun extractText(message: ChatCompletionMessage): String? {
			val raw = message._content().asUnknown().orElse(null) ?: return null
			raw.asString().orElse(null)?.let { return it }
			return raw.asArray().orElse(null)
				?.filter { chunk -> chunk.asObject().orElse(null)?.get("type")?.asString()?.orElse(null) == "text" }
				?.joinToString("") { chunk -> chunk.asObject().orElse(null)?.get("text")?.asString()?.orElse("") ?: "" }
				?.ifEmpty { null }
		}
	}

	val client: OpenAIClient = OpenAIOkHttpClient.builder()
		.baseUrl(BASE_URL)
		.apiKey(token)
		.build()

	fun generateMessage(prompt: String, userMessage: String): String? {
		val params = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.reasoningEffort(ReasoningEffort.LOW)
			.addSystemMessage(prompt)
			.addUserMessage(userMessage)
			.build()

		val completion = runCatching { client.chat().completions().create(params) }.getOrNull()
			?: return null

		return completion.choices()
			.firstOrNull()
			?.message()
			?.let { extractText(it) }
			?: ""
	}
}