package commands.helpers

object Execution {
	val executees: MutableSet<String> = mutableSetOf()

	fun execute(userID: String) = executees.add(userID)
	fun isExecuted(userID: String) = executees.contains(userID)
	fun revive(userID: String) = executees.remove(userID)
	fun reviveAll() = executees.clear()
}