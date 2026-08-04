package ai

import Config.Snowflakes.ahaNickname
import ai.helpers.MessageQueue
import com.openai.models.chat.completions.ChatCompletionCreateParams
import dev.kord.core.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

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
		userMessage = message
	)

	suspend fun generateMessage(messageHistory: MessageQueue, prompt: String, userMessage: Message): String? {
		mutex.withLock {
			val paramsBuilder = ChatCompletionCreateParams.builder()
				.model(MODEL_ID)
				.addSystemMessage(prompt)

			if (!messageHistory.messages.any { m -> m.msg == userMessage }) messageHistory.addMessage(
				userMessage,
				MessageQueue.Type.UserMessage
			)

			messageHistory.messages.forEach {
				when (it.type) {
					MessageQueue.Type.UserMessage -> paramsBuilder.addUserMessage("${it.msg.author?.ahaNickname() ?: it.msg.data.author.username}: ${it.msg.content}")
					MessageQueue.Type.AIMessage -> paramsBuilder.addAssistantMessage("${it.msg.author?.ahaNickname() ?: it.msg.data.author.username}: ${it.msg.content}")
				}
			}

			val params = paramsBuilder.build()

			val completion = withTimeoutOrNull(15.seconds) {
				runInterruptible(Dispatchers.IO) {
					client.chat().completions().create(params)
				}
			}

			val aiMessage = completion?.choices()
				?.firstOrNull()
				?.message()
				?.content()
				?.getOrNull()

			return aiMessage
		}
	}
}