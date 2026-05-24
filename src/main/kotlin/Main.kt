
import commands.aiGames.tictactoe.TicTacToeSession
import commands.autoComplete.AutoCompletes
import commands.buttonCommands.ButtonCommands
import commands.modalCommands.ModalCommands
import commands.slashCommands.SlashCommands
import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import messages.MessageHandler

const val RECREATE = false

suspend fun main() {
	val kord = Kord(Config.Tokens.botToken)

	TicTacToeSession.loadAI()

	recreateCommands(kord)
	SlashCommands.registerAll(kord)
	ButtonCommands.registerAll(kord)
	ModalCommands.registerAll(kord)
	AutoCompletes.registerAll(kord)

	MessageHandler.init(kord)

	kord.login {
		presence { playing("I was forced to do this") }
		@OptIn(PrivilegedIntent::class)
		intents += Intent.MessageContent

		val channel = kord.getChannelOf<GuildMessageChannel>(Config.Snowflakes.Channels.botChannelID)
        channel?.createMessage("https://tenor.com/view/wwe-coffin-world-wrestling-entertainment-gif-17903370")
	}
}

private suspend fun recreateCommands(kord: Kord) {
	if (RECREATE) {
		SlashCommands.deleteOld(kord)
		SlashCommands.createAll(kord)
	}
}