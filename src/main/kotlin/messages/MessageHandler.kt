package messages

import commands.ai.LLM
import commands.helpers.Execution
import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import kotlin.random.Random

class MessageHandler {
	fun init(kord: Kord) {
		kord.on<MessageCreateEvent> {
			val author = message.author ?: return@on
			if (author.isBot) return@on

			if (Random.nextInt(100) == 0) {
				val verger = kord.getGuild(SlashCommand.guildID).getEmoji(Snowflake(1225937868023795792))
				message.addReaction(verger)
			}

			if (message.content.contains("Scorch")) {
				message.reply { content = LLM.generateMessage(message.content) }
			}

			val authorId = author.id.value.toString()
			if (Execution.isExecuted(authorId)) {
				message.delete()
			} else {
				TicTacToeMessages.evaluate(kord, message)
			}
		}
	}
}