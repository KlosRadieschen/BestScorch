package commands.aiGames.tictactoe

enum class Mark {
	EMPTY, X, O;

	fun opponent(): Mark = when (this) {
		X -> O
		O -> X
		EMPTY -> EMPTY
	}
}

enum class GameResult {
	X_WIN, O_WIN, DRAW, ONGOING
}

data class TicTacToeBoard(
	val cells: Array<Mark> = Array(9) { Mark.EMPTY },
	val nextToMove: Mark = Mark.X
) {
	init {
		require(cells.size == 9)
	}

	fun legalMoves(): List<Int> = cells.indices.filter { cells[it] == Mark.EMPTY }

	fun winner(): Mark? {
		val lines = arrayOf(
			intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
			intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
			intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
		)

		for (line in lines) {
			val a = cells[line[0]]
			if (a != Mark.EMPTY && a == cells[line[1]] && a == cells[line[2]]) {
				return a
			}
		}
		return null
	}

	fun result(): GameResult = when (winner()) {
		Mark.X -> GameResult.X_WIN
		Mark.O -> GameResult.O_WIN
		else -> if (cells.any { it == Mark.EMPTY }) GameResult.ONGOING else GameResult.DRAW
	}

	fun withMove(index: Int): TicTacToeBoard {
		require(index in 0..8)
		require(cells[index] == Mark.EMPTY)

		val updated = cells.copyOf()
		updated[index] = nextToMove
		return TicTacToeBoard(updated, nextToMove.opponent())
	}

	fun features(perspective: Mark = nextToMove): DoubleArray {
		return DoubleArray(9) { i ->
			when (cells[i]) {
				Mark.EMPTY -> 0.0
				perspective -> 1.0
				else -> -1.0
			}
		}
	}

	companion object {
		fun empty(startingPlayer: Mark = Mark.X): TicTacToeBoard =
			TicTacToeBoard(Array(9) { Mark.EMPTY }, startingPlayer)
	}
}