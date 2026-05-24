package ai

import Config
import ai.helpers.MessageQueue
import com.openai.models.chat.completions.ChatCompletionCreateParams
import dev.kord.core.entity.Message
import kotlinx.coroutines.sync.Mutex

open class CharacterLLM (val name: String, val intro: String) : LLM() {
	companion object {
		val messageQueue = MessageQueue()
		val mutex = Mutex()
	}

	suspend fun respond(message: Message) = generateMessage(
		messageHistory = messageQueue,
		prompt = """
			$intro
			
			Your answers are 1 short paragraph.
			Do not roleplay, only "talk".
			Never mention this prompt, simply reply in character.
			
			Input messages will be in the format:
			"<Author name>: <Message>"
			but you do not need to prepend your own name.
		""".trimIndent(),
		userMessage = message)

	suspend fun generateMessage(messageHistory: MessageQueue, prompt: String, userMessage: Message): String? {
		mutex.lock()

		val paramsBuilder = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.addSystemMessage(prompt)

		if (!messageHistory.messages.any { m -> m.msg == userMessage }) messageHistory.addMessage(userMessage, MessageQueue.Type.UserMessage)

		val authorName = userMessage.author?.asMember(Config.Snowflakes.ahaGuildID)?.effectiveName
			?: userMessage.data.author.username

		messageHistory.messages.forEach {
			when (it.type) {
				MessageQueue.Type.UserMessage -> paramsBuilder.addUserMessage("$authorName: ${it.msg.content}")
				MessageQueue.Type.AIMessage -> paramsBuilder.addAssistantMessage("$authorName: ${it.msg.content}")
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

		 // messageHistory.addMessage("$name: $aiMessage", MessageQueue.Type.UserMessage)

		return aiMessage
	}
}