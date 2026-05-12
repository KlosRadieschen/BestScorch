package commands.aiGames.tictactoe

import smile.classification.MLP
import java.io.*
import java.util.*
import kotlin.math.max

private const val LABEL_LOSS = 0
private const val LABEL_DRAW = 1
private const val LABEL_WIN = 2

data class Sample(
	val x: DoubleArray,
	val y: Int,
	val weight: Int
) : Serializable

data class TicTacToeAiSnapshot(
	val replay: List<Sample>,
	val gamesSinceRetrain: Int
) : Serializable

class NeuralNetwork(
	private val hiddenLayers: String = "ReLU(32)",
	private val minSamplesToTrain: Int = 5,
	private val maxReplaySize: Int = 5000,
) {
	private val replay = ArrayList<Sample>()
	private val episodeBoards = ArrayList<TicTacToeBoard>()

	private var gamesSinceRetrain = 0
	private var model: MLP? = null

	public fun startEpisode() {
		episodeBoards.clear()
	}

	public fun observe(board: TicTacToeBoard) {
		episodeBoards += board
	}

	public fun finishEpisode(result: GameResult) {
		if (episodeBoards.isNotEmpty()) {
			val total = episodeBoards.size

			episodeBoards.forEachIndexed { index, board ->
				val baseLabel = labelFor(board, result)
				val pliesRemaining = (total - 1 - index).coerceAtLeast(0)

				val weight = when (baseLabel) {
					LABEL_WIN -> max(1, 1 + pliesRemaining)
					LABEL_LOSS -> max(1, 1 + pliesRemaining / 2)
					else -> 1
				}

				replay += Sample(
					x = board.features(),
					y = baseLabel,
					weight = weight
				)
			}

			while (replay.size > maxReplaySize) {
				replay.removeAt(0)
			}

			episodeBoards.clear()
			retrain()

			save("src/main/resources/brain.ser")
		}
	}

	public fun chooseMove(board: TicTacToeBoard): Int {
		require(board.result() == GameResult.ONGOING)
		val legal = board.legalMoves()
		require(legal.isNotEmpty())

		val m = model ?: return heuristicMove(board, legal)

		return legal.maxByOrNull { move ->
			val afterMyMove = board.withMove(move)
			val opponentPerspective = afterMyMove.features(afterMyMove.nextToMove)
			val predicted = m.predict(opponentPerspective)

			var score = when (predicted) {
				LABEL_LOSS -> 2.0
				LABEL_DRAW -> 1.0
				LABEL_WIN -> 0.0
				else -> 1.0
			}

			val me = board.nextToMove
			val opponent = me.opponent()

			if (afterMyMove.winner() == me) {
				score += 3.0
			}

			val opponentCanWinImmediately = afterMyMove.legalMoves().any { reply ->
				afterMyMove.withMove(reply).winner() == opponent
			}

			if (!opponentCanWinImmediately) {
				score += 1.5
			} else {
				score -= 1.5
			}

			score += moveBias(move)
			score
		} ?: legal.first()
	}

	public fun recordAndChooseMove(board: TicTacToeBoard): Int {
		observe(board)
		return chooseMove(board)
	}

	public fun retrain() {
		if (replay.size < minSamplesToTrain) return

		val expandedX = ArrayList<DoubleArray>()
		val expandedY = ArrayList<Int>()

		for (sample in replay) {
			repeat(sample.weight) {
				expandedX += sample.x
				expandedY += sample.y
			}
		}

		if (expandedX.size < minSamplesToTrain) return
		if (expandedY.distinct().size < 2) return

		val props = Properties().apply {
			setProperty("smile.mlp.layers", hiddenLayers)
			setProperty("smile.mlp.epochs", "20")
			setProperty("smile.mlp.mini_batch", "16")
			setProperty("smile.mlp.learning.rate", "0.01")
			setProperty("smile.mlp.momentum", "0.9")
			setProperty("smile.mlp.weight.decay", "0.0001")
		}

		model = MLP.fit(expandedX.toTypedArray(), expandedY.toIntArray(), props)
	}

	private fun labelFor(board: TicTacToeBoard, result: GameResult): Int {
		return when (result) {
			GameResult.DRAW -> LABEL_DRAW
			GameResult.X_WIN -> if (board.nextToMove == Mark.X) LABEL_WIN else LABEL_LOSS
			GameResult.O_WIN -> if (board.nextToMove == Mark.O) LABEL_WIN else LABEL_LOSS
			GameResult.ONGOING -> LABEL_DRAW
		}
	}

	private fun heuristicMove(board: TicTacToeBoard, legal: List<Int>): Int {
		return legal.maxByOrNull { moveBias(it) } ?: legal.first()
	}

	private fun moveBias(move: Int): Double = when (move) {
		4 -> 0.2
		0, 2, 6, 8 -> 0.1
		else -> 0.0
	}

	public fun save(path: String) {
		ObjectOutputStream(FileOutputStream(path)).use { out ->
			out.writeObject(model)
			out.writeObject(TicTacToeAiSnapshot(replay.toList(), gamesSinceRetrain))
		}
	}

	public fun load(path: String) {
		ObjectInputStream(FileInputStream(path)).use { input ->
			@Suppress("UNCHECKED_CAST")
			model = input.readObject() as? MLP

			val snapshot = input.readObject() as TicTacToeAiSnapshot
			replay.clear()
			replay.addAll(snapshot.replay)
			gamesSinceRetrain = snapshot.gamesSinceRetrain
		}
	}
}