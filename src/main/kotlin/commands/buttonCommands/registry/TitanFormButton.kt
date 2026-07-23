package commands.buttonCommands.registry

import commands.buttonCommands.ButtonCommand
import database.Database
import database.tables.TitanEntity
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondEphemeral
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object TitanFormButton : ButtonCommand (
	id = "titan-form",
	run = commandRun@{
		val callsign = componentId.split(":")[1].split("~")[0]
		val formChoice = componentId.split(":")[1].split("~")[1]

		val titan = transaction(Database.db) {
			TitanEntity.findById(callsign)!!
		}

		if (titan.ownerID != user.id.value) {
			respondEphemeral { content = "You don't own this titan, bozo" }
			return@commandRun
		}

		modal("$formChoice Info Form", componentId) {
			fun addModalInput(
				label: String,
				style: TextInputStyle,
				currentValue: String?
			) {
				label(label) {
					textInput(style, label.lowercase()) {
						value = currentValue.orEmpty()
						required = false
					}
				}
			}

			when (formChoice) {
				"basic" -> {
					transaction {
						addModalInput("Name", TextInputStyle.Short, titan.name)
						addModalInput("Chassis", TextInputStyle.Short, titan.chassis)
						addModalInput("Class", TextInputStyle.Short, titan.titanClass)
						addModalInput("Kits", TextInputStyle.Short, titan.kits)
					}
				}

				"military" -> {
					transaction {
						addModalInput("Primary Weapon", TextInputStyle.Short, titan.primaryWeapon)
						addModalInput("Ordinance", TextInputStyle.Short, titan.ordinance)
						addModalInput("Defensive Ability", TextInputStyle.Short, titan.defensive)
						addModalInput("Tactical Ability", TextInputStyle.Short, titan.tactical)
						addModalInput("Core", TextInputStyle.Short, titan.core)
					}
				}

				"lore" -> {
					transaction {
						addModalInput("Description", TextInputStyle.Paragraph, titan.description)
						addModalInput("Design/Appearance", TextInputStyle.Paragraph, titan.design)
						addModalInput("Misc", TextInputStyle.Paragraph, titan.misc)
					}
				}
			}
		}
	}
)