package commands.buttonCommands.registry

import commands.buttonCommands.ButtonCommand
import commands.helpers.Arena
import commands.helpers.Execution.execute
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.rest.builder.component.actionRow
import kotlin.random.Random

@Suppress("UNUSED")
object ChallengeButton : ButtonCommand (
	id = "challenge",
	run = commandRun@{
		val option = componentId.split(":")[1].split("~")[0]
		val uuid = componentId.split(":")[1].split("~")[1]

		val fight = Arena.fights[uuid]
		if (fight == null) {
			respondEphemeral { content = "Fight over" }
			return@commandRun
		}

		val challenger = fight.challenger
		val challengee = fight.challengee

		if (user != challengee.user && user != challenger.user) {
			respondEphemeral { content = "This challenge is not for you, bozo" }
			return@commandRun
		}

			when (option) {
				"acc" -> {
					if (user != challengee.user) {
						respondEphemeral { content = "This challenge is not for you, bozo" }
						return@commandRun
					}

					val message = respondPublic {
						content = """
						# ${challenger.user.mention} VS ${challengee.user.mention}
						
						${challenger.user.mention}: ${challenger.result ?: "⏳"}
						${challengee.user.mention}: ${challengee.result ?: "⏳"}
					""".trimIndent()

						actionRow {
							interactionButton(ButtonStyle.Primary, "challenge:roll~$uuid") {
								label = "Roll"
							}
						}
					}

					fight.message = message
				}

				"dec" -> {
					if (user != challengee.user) {
						respondEphemeral { content = "You can't accept this challenge" }
						return@commandRun
					}

					respondPublic {
						content = "${challengee.user.mention} chickened out of a duel with ${challenger.user.mention}"
					}

					Arena.stopFight(uuid)
				}

				"roll" -> {
					if (user.id == challenger.user.id && challenger.result == null) {
						fight.challenger.result = Random.nextInt(1, 21)
					} else if (user.id == challengee.user.id && challengee.result == null) {
						fight.challengee.result = Random.nextInt(1, 21)
					} else {
						respondEphemeral { content = "Invalid roll" }
						return@commandRun
					}

					fight.message!!.edit {
						content = buildString {
							appendLine("""
								# ${challenger.user.mention} VS ${challengee.user.mention}
						
								${challenger.user.mention}: ${challenger.result ?: "⏳"}
								${challengee.user.mention}: ${challengee.result ?: "⏳"}
								
							""".trimIndent())

							if (challenger.result != null && challengee.result != null) {
								if (challenger.result!! > challengee.result!!) {
									appendLine("**${challenger.user.mention} won, ${challengee.user.mention} dies**")
									challengee.user.execute()
								} else if (challenger.result!! < challengee.result!!) {
									 appendLine("**${challengee.user.mention} won, ${challenger.user.mention} dies**")
									challenger.user.execute()
								} else {
									appendLine("**It's a tie, both die**")
									challenger.user.execute()
									challengee.user.execute()
								}

								Arena.stopFight(uuid)
							}
						}

						actionRow {
							interactionButton(ButtonStyle.Primary, "challenge:roll~$uuid") {
								label = "Roll"
								if (challenger.result != null && challengee.result != null) { disabled = true }
							}
						}
					}
					respondEphemeral { content = "Roll added" }
				}
			}


	}
)