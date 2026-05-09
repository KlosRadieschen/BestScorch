package messages

import commands.slashCommands.SlashCommand
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import kotlin.random.Random

object RandomReactor {
	const val REACTIONCHANCE = 100

	suspend fun randomReact(kord: Kord, message: Message) {
		if (Random.nextInt(REACTIONCHANCE) == 0) {
			val ids = listOf(
				1225937868023795792,
				1317944817757589536,
				1263635500837769337,
				1317567994326417518,
				1267600311955095727,
				1284989907043090484,
				1251566109463810132,
				1249143603897434153,
				1251218013873639577,
				1264006545994416159,
				1259237648812478505,
				1253725248050954442,
				1342999244554108958,
				1314288228198518886,
				1283879049252311141,
				1227319169876496404,
				1260981505597898895,
				1467617301439189083,
				1301868886362165359
			)

			val emoji = kord.getGuild(SlashCommand.guildID).getEmoji(Snowflake(ids.random()))
			message.addReaction(emoji)
		}
	}
}