import commands.aiGames.tictactoe.TicTacToeSession
import commands.slashCommands.SlashCommands
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import messages.MessageHandler

suspend fun main() {
	val kord = Kord(Dotenv.load().get("BOT_TOKEN"))

	TicTacToeSession.loadAI()

	//SlashCommands.deleteOld(kord)
	//SlashCommands.createAll(kord)
	SlashCommands.registerAll(kord)

	MessageHandler.init(kord)

	kord.login {
		presence { playing("I was forced to do this") }
		@OptIn(PrivilegedIntent::class)
		intents += Intent.MessageContent

		val channel = kord.getChannelOf<GuildMessageChannel>(Snowflake(Dotenv.load().get("BOT_CHANNEL_ID")!!))
        channel?.createMessage("https://tenor.com/view/wwe-coffin-world-wrestling-entertainment-gif-17903370")
	}
}
