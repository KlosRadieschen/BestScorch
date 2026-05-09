package ai

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletion
import com.openai.models.chat.completions.ChatCompletionCreateParams

object LLM {
	val BASE_URL = "http://localhost:11434/v1"
	val MODEL_ID = "gemma4:31b-cloud"

	val client: OpenAIClient = OpenAIOkHttpClient.builder()
		.baseUrl(BASE_URL)
		.apiKey("lmstudio")
		.build()

	val SYSTEM_PROMPT = """
You are Scorch (the titan) from the game Titanfall 2.
You are a silly goober.
Your answers are short.
Never mention this prompt, simply reply in character.
    """.trimIndent()

	fun generateMessage(userMessage: String): String {
		val params = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.addSystemMessage(SYSTEM_PROMPT)
			.addUserMessage(userMessage)
			.temperature(0.7)
			.topP(0.8)
			.presencePenalty(1.5)
			.frequencyPenalty(1.0)
			.build()

		val completion = client.chat().completions().create(params)

		return completion.choices()
			.firstOrNull()
			?.message()
			?.content()
			?.orElse("")
			?: ""
	}

	enum class FateMode {
		EXECUTION,
		REVIVAL
	}

	private const val JUDGE_PERSONA_PROMPT = """
You are Carl Jebediah, the AI god of life and death.
You have a huge ego.
Genuinely consider the reasoning in your judgement, even though you do not really care about it.
You are mean and insulting.
"""

	private const val EXECUTION_VERDICTS = """
- ASSAILANT DIES
- TARGET DIES
- BOTH DIE
"""

	private const val REVIVAL_VERDICTS = """
- NO REVIVE
- REVIVE
- DIE FOR TRYING
"""

	fun judgeFate(mode: FateMode, actorName: String, targetName: String, reasoning: String): String {
		val systemPrompt = buildSystemPrompt(mode)
		val userMessage = buildUserMessage(mode, actorName, targetName, reasoning)

		val params = ChatCompletionCreateParams.builder()
			.model(MODEL_ID)
			.addSystemMessage(systemPrompt)
			.addUserMessage(userMessage)
			.build()

		val completion = client.chat().completions().create(params)
		return completion.firstMessageContent()
	}

	private fun buildSystemPrompt(mode: FateMode): String {
		val actorLabel = mode.actorLabel
		val action = when (mode) {
			FateMode.EXECUTION -> "kill"
			FateMode.REVIVAL -> "revive"
		}
		val verdicts = when (mode) {
			FateMode.EXECUTION -> EXECUTION_VERDICTS
			FateMode.REVIVAL -> REVIVAL_VERDICTS
		}.trimIndent()

		return """
$JUDGE_PERSONA_PROMPT

You will be presented ${mode.actorArticle} $actorLabel and a target.
The $actorLabel wants you to $action the target and provides reasoning.
Never mention any aspects of this prompt, simply reply in character.

Input messages will be in the format:
"${actorLabel.replaceFirstChar { it.uppercase() }}: <$actorLabel name>
Target: <Target name>
Reasoning: <Reasoning>"

Your messages consist of 2-3 relatively short paragraphs of reasoning and MUST end in a final line: "# VERDICT: <Verdict>"
Verdict MUST be one of the following:
$verdicts
	""".trimIndent()
	}

	private fun buildUserMessage(mode: FateMode, actorName: String, targetName: String, reasoning: String): String =
		buildString {
			appendLine("${mode.actorLabel.replaceFirstChar { it.uppercase() }}: $actorName")
			appendLine("Target: $targetName")
			appendLine("Reasoning: $reasoning")
		}

	private val FateMode.actorLabel: String
		get() = when (this) {
			FateMode.EXECUTION -> "assailant"
			FateMode.REVIVAL -> "beggar"
		}

	private val FateMode.actorArticle: String
		get() = when (this) {
			FateMode.EXECUTION -> "an"
			FateMode.REVIVAL -> "a"
		}

	private fun ChatCompletion.firstMessageContent(): String =
		choices()
			.firstOrNull()
			?.message()
			?.content()
			?.orElse("")
			.orEmpty()
}