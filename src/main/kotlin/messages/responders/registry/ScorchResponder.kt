package messages.responders.registry

import ai.systemCharacters.Scorch
import commands.slashCommands.SlashCommand
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import messages.responders.Responder

object ScorchResponder : Responder(
	check = { content.contains("Scorch") || referencedMessage?.author?.id == kord.selfId },
	execute = {
		channel.withTyping {
			val llmResponse =  Scorch.respond("${author?.asMember(SlashCommand.guildID)!!.effectiveName}: ${content.orEmpty()}")

			reply { content = llmResponse ?: "AI is not responding <:verger:1225937868023795792>" }
		}
	}
)