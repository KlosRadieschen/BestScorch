package ai

import ai.helpers.MessageQueue
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams
import kotlinx.coroutines.sync.Mutex

abstract class LLM {
	companion object {
		const val BASE_URL = "http://localhost:11434/v1"
		const val MODEL_ID = "gemma4:31b-cloud"
		val mutex = Mutex()
	}

	val client: OpenAIClient = OpenAIOkHttpClient.builder()
		.baseUrl(BASE_URL)
		.apiKey("lmstudio")
		.build()

	suspend fun generateMessage(prompt: String, userMessage: String): String? {
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

	suspend fun generateMessage(messageHistory: MessageQueue, prompt: String, userMessage: String): String? {
		mutex.lock()

		val paramsBuilder = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.addSystemMessage(prompt)

		messageHistory.addMessage(userMessage, MessageQueue.Type.UserMessage)

		messageHistory.messages.forEach {
			when (it.type) {
				MessageQueue.Type.UserMessage -> paramsBuilder.addUserMessage(it.msg)
				MessageQueue.Type.AIMessage -> paramsBuilder.addAssistantMessage(it.msg)
			}
		}

		val params = paramsBuilder.build()

		val completion = runCatching { client.chat().completions().create(params) }.getOrNull()
			?: return null

		val aiMessage = completion.choices()
			.firstOrNull()
			?.message()
			?.content()
			?.orElse("")
			?: ""

		messageHistory.addMessage(aiMessage, MessageQueue.Type.AIMessage)

		mutex.unlock()

		return aiMessage
	}
}