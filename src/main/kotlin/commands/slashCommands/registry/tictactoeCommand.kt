package commands.slashCommands.registry

import Config
import commands.aiGames.tictactoe.Mark
import commands.aiGames.tictactoe.TicTacToeSession
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.channel.GuildMessageChannel
import kotlin.random.Random

object TictactoeCommand : SlashCommand(
	name = "tictactoe",
	description = "Play against the smartest AI in human history",
	args = {

	},
	run = {
		val startingPlayer = if (Random.nextBoolean()) Mark.X else Mark.O

		TicTacToeSession.games[user.id.value.toString()] = TicTacToeSession(TicTacToeSession.ai, startingPlayer)

		if (startingPlayer == Mark.O) TicTacToeSession.games[user.id.value.toString()]!!.applyAiMove()

		val chan = kord.getChannelOf<GuildMessageChannel>(Config.Snowflakes.Channels.botChannelID)!!
		chan.createMessage("${user.mention} type a number in the chat to place your mark")
		chan.createMessage(TicTacToeSession.games[user.id.value.toString()]!!.toHumanReadable())

		respondEphemeral { content = "Game started" }

		null
	}
)