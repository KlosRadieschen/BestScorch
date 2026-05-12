package commands.aiGames.tictactoe

class TicTacToeSession(
	private val ai: NeuralNetwork = NeuralNetwork(),
	startingPlayer: Mark = Mark.X
) {
	private var board: TicTacToeBoard = TicTacToeBoard.empty(startingPlayer)

	public fun currentBoard(): TicTacToeBoard = board

	public fun toHumanReadable(): String {
		val numEmojis = listOf("1\uFE0F⃣","2\uFE0F⃣","3\uFE0F⃣ ","4\uFE0F⃣","5\uFE0F⃣","6\uFE0F⃣","7\uFE0F⃣","8\uFE0F⃣","9\uFE0F⃣")

		return buildString {
			board.cells.forEachIndexed { i, cell ->
				when (cell) {
					Mark.X -> append("<:verger:1225937868023795792>")
					Mark.O -> append("<:chad:1263635500837769337>")
					Mark.EMPTY -> append(numEmojis[i])
				}

				if (i % 3 == 2) append("\n")
			}
		}
	}

	public fun reset(startingPlayer: Mark = Mark.X) {
		board = TicTacToeBoard.empty(startingPlayer)
		ai.startEpisode()
	}

	public fun applyHumanMove(index: Int): Boolean {
		if (board.result() != GameResult.ONGOING) return false
		if (board.nextToMove != Mark.X) return false

		return applyMove(index)
	}

	public fun applyAiMove(): Int? {
		if (board.result() != GameResult.ONGOING) return null
		if (board.nextToMove != Mark.O) return null

		val move = ai.recordAndChooseMove(board)
		applyMove(move)
		return move
	}

	public fun finishGame(): GameResult {
		val result = board.result()
		if (result != GameResult.ONGOING) {
			ai.finishEpisode(result)
		}
		return result
	}

	private fun applyMove(index: Int): Boolean {
		return try {
			board = board.withMove(index)
			true
		} catch (_: IllegalArgumentException) {
			false
		}
	}

	companion object {
		val ai = NeuralNetwork()
		val games = mutableMapOf<String, TicTacToeSession>()

		public fun loadAI() {
			ai.load("src/main/resources/brain.ser")
		}
	}
}