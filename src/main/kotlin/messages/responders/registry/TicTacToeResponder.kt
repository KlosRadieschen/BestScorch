package messages.responders.registry

import commands.aiGames.tictactoe.GameResult
import commands.aiGames.tictactoe.TicTacToeSession
import commands.helpers.Execution.isExecuted
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import io.github.cdimascio.dotenv.Dotenv
import messages.responders.Responder
import messages.responders.registry.TicTacToeResponder.checkWin

object TicTacToeResponder : Responder(
	check = { channelId.value.toString() == Dotenv.load().get("BOT_CHANNEL_ID") && TicTacToeSession.Companion.games.containsKey(author!!.id.value.toString()) && !(author?.isExecuted()?:false) },
	execute = execute@{
		val playerMove = try {
			content.toInt().coerceIn(1, 9) - 1
		} catch (e: NumberFormatException) {
			reply { content = "That's not a number you bozo" }
			return@execute
		}

		val session = TicTacToeSession.Companion.games[author!!.id.value.toString()]!!

		session.applyHumanMove(playerMove)
		if (checkWin(kord, this, session)) return@execute
		session.applyAiMove()
		if (checkWin(kord, this, session)) return@execute

		reply { content = session.toHumanReadable() }
	}
) {
	private suspend fun checkWin(kord: Kord, message: Message, session: TicTacToeSession): Boolean {
		if (session.currentBoard().result() != GameResult.ONGOING) {
			val result = session.finishGame()

			when (result) {
				GameResult.X_WIN -> message.reply { content = "${message.author!!.mention} won against the clanker" }
				GameResult.O_WIN -> message.reply { content = "${message.author!!.mention} just lost to a dirty clanker" }
				else -> message.reply { content = "${message.author!!.mention} drew with the clanker" }
			}

			TicTacToeSession.Companion.games.remove(message.author!!.id.value.toString())
			message.reply { content = session.toHumanReadable() }

			return true
		}
		return false
	}
}