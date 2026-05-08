package messages

import commands.aiGames.tictactoe.GameResult
import commands.aiGames.tictactoe.TicTacToeSession
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import io.github.cdimascio.dotenv.Dotenv

object TicTacToeMessages {
	public suspend fun evaluate(kord: Kord, message: Message) {
		if (message.channelId.value.toString() == Dotenv.load().get("BOT_CHANNEL_ID") && TicTacToeSession.games.containsKey(message.author!!.id.value.toString())) {
			val playerMove = try {
				message.content.toInt().coerceIn(1, 9) - 1
			} catch (e: NumberFormatException) {
				message.reply { content = "That's not a number you bozo" }
				return
			}

			val session = TicTacToeSession.games[message.author!!.id.value.toString()]!!

			session.applyHumanMove(playerMove)
			if (checkWin(kord, message, session)) return
			session.applyAiMove()
			if (checkWin(kord, message, session)) return

			message.reply { content = session.toHumanReadable() }
		}
	}

	private suspend fun checkWin(kord: Kord, message: Message, session: TicTacToeSession): Boolean {
		if (session.currentBoard().result() != GameResult.ONGOING) {
			val result = session.finishGame()

			when (result) {
				GameResult.X_WIN -> message.reply { content = "${message.author!!.mention} won against the clanker" }
				GameResult.O_WIN -> message.reply { content = "${message.author!!.mention} just lost to a dirty clanker" }
				else -> message.reply { content = "${message.author!!.mention} drew with the clanker" }
			}

			TicTacToeSession.games.remove(message.author!!.id.value.toString())
			message.reply { content = session.toHumanReadable() }

			return true
		}
		return false
	}
}