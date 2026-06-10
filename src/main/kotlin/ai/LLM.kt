package ai

import Config
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams

abstract class LLM {
	companion object {
		val token = Config.Tokens.mistralToken
		const val BASE_URL = "http://127.0.0.1:1234/v1"
		const val MODEL_ID = "google/gemma-4-12b-qat"
	}

	val client: OpenAIClient = OpenAIOkHttpClient.builder()
		.baseUrl(BASE_URL)
		.apiKey(token)
		.build()

	fun generateMessage(prompt: String, userMessage: String): String? {
		val params = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.addSystemMessage(prompt)
			.addUserMessage(userMessage)
			.build()

		val completion = runCatching { client.chat().completions().create(params) }.getOrNull()
			?: return null

		return completion.choices()
			.firstOrNull()
			?.message()
			?.content()
			?.orElse("")
			?: ""
	}
}