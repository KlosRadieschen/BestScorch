package commands.slashCommands.registry

import commands.aiGames.tictactoe.Mark
import commands.aiGames.tictactoe.TicTacToeSession
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.cdimascio.dotenv.Dotenv
import kotlin.random.Random

object TictactoeCommand : SlashCommand(
	name = "tictactoe",
	description = "Play against the smartest AI in human history",
	args = {

	},
	run = { response ->
		val startingPlayer = if (Random.nextBoolean()) Mark.X else Mark.O
		TicTacToeSession.games[user.id.value.toString()] = TicTacToeSession(TicTacToeSession.ai, startingPlayer)
		if (startingPlayer == Mark.O) TicTacToeSession.games[user.id.value.toString()]!!.applyAiMove()
		val chan = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")!!))!!
		chan.createMessage("${user.mention} type a number in the chat to place your mark")
		chan.createMessage(TicTacToeSession.games[user.id.value.toString()]!!.toHumanReadable())
		response.respond { content = "Game started" }
	}
)