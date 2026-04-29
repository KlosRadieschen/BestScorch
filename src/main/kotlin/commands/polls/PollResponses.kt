package commands.polls

sealed interface PollResponses {
	data class Options(
		val values: List<String>,
		val votes: IntArray
	) : PollResponses

	data object Discussion : PollResponses

	data class Inputs(
		val responses: MutableList<String> = mutableListOf()
	) : PollResponses
}