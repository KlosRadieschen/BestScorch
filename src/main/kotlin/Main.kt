import commands.aiGames.tictactoe.GameResult
import commands.aiGames.tictactoe.Mark
import commands.aiGames.tictactoe.TicTacToeSession
import dev.kord.core.Kord
import commands.slashCommands.SlashCommands
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import messages.MessageHandler
import java.util.concurrent.atomic.AtomicBoolean

suspend fun main() {
	val kord = Kord(Dotenv.load().get("BOT_TOKEN"))

	TicTacToeSession.loadAI()

	val slashCommands = SlashCommands()
	//slashCommands.deleteOld(kord)
	//slashCommands.createAll(kord)
	slashCommands.registerAll(kord)

	val messageHandler = MessageHandler()
	messageHandler.init(kord)


	kord.login {
		presence { playing("I was forced to do this") }
		@OptIn(PrivilegedIntent::class)
		intents += Intent.MessageContent
	}
}
