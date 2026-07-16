package commands.buttonCommands.registry

import commands.buttonCommands.ButtonCommand
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondEphemeral
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object CharacterFormButton : ButtonCommand (
	id = "character-form",
	run = commandRun@{
		val characterName = componentId.split(":")[1].split("-")[0]
		val formChoice = componentId.split(":")[1].split("-")[1]

		val character = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.sanitizedName eq characterName }.first()
		}

		if (character.ownerID != user.id.value) {
			respondEphemeral { content = "You don't own this character, bozo" }
			return@commandRun
		}

		modal("$formChoice Info Form", componentId) {
			when (formChoice) {
				"basic" -> {
					label("Age"){
						textInput(TextInputStyle.Short, "age") {
							value = character.age.orEmpty()
							placeholder = character.age.orEmpty()
							required = false
						}
					}

					label("Gender"){
						textInput(TextInputStyle.Short, "gender") {
							value = character.gender.orEmpty()
							placeholder = character.gender.orEmpty()
							required = false
						}
					}

					label("Height"){
						textInput(TextInputStyle.Short, "height") {
							value = character.height.orEmpty()
							placeholder = character.height.orEmpty()
							required = false
						}
					}

					label("Appearance"){
						textInput(TextInputStyle.Paragraph, "appearance") {
							value = character.appearance.orEmpty()
							placeholder = character.appearance.orEmpty()
							required = false
						}
					}
				}

				"military" -> {
					label("Battalion"){
						textInput(TextInputStyle.Short, "battalion") {
							value = character.battalion.orEmpty()
							placeholder = character.battalion.orEmpty()
							required = false
						}
					}

					label("Rank"){
						textInput(TextInputStyle.Short, "rank") {
							value = character.rank.orEmpty()
							placeholder = character.rank.orEmpty()
							required = false
						}
					}

				}

				"lore" -> {
					label("Lore"){
						textInput(TextInputStyle.Paragraph, "lore") {
							value = character.lore.orEmpty()
							placeholder = character.lore.orEmpty()
							required = false
						}
					}

					label("Traits"){
						textInput(TextInputStyle.Paragraph, "traits") {
							 value = character.traits.orEmpty()
							placeholder = character.traits.orEmpty()
							required = false
						}
					}

					label("Likes"){
						textInput(TextInputStyle.Short, "likes") {
							value = character.likes.orEmpty()
							placeholder = character.likes.orEmpty()
							required = false
						}
					}

					label("Dislikes"){
						textInput(TextInputStyle.Short, "dislikes") {
							value = character.dislikes.orEmpty()
							placeholder = character.dislikes.orEmpty()
							required = false
						}
					}

					label("Misc"){
						textInput(TextInputStyle.Paragraph, "misc") {
							value = character.misc.orEmpty()
							placeholder = character.misc.orEmpty()
							required = false
						}
					}
				}
			}
		}
	}
)