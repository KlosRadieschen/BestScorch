package ai

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams
import io.github.cdimascio.dotenv.Dotenv

abstract class LLM {
	companion object {
		val token = Config.Tokens.mistralToken
		const val BASE_URL = "https://api.mistral.ai/v1/"
		const val MODEL_ID = "mistral-medium-2508"
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