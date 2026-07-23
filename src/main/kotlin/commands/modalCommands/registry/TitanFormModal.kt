package commands.modalCommands.registry

import commands.modalCommands.ModalCommand
import database.Database
import database.tables.TitanEntity
import database.tables.TitanTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("UNUSED")
object TitanFormModal : ModalCommand(
	id = "titan-form",
	run = {
		val callsign = modalId.split(":")[1].split("~")[0]
		val formChoice = modalId.split(":")[1].split("~")[1]

			transaction(Database.db) {
				TitanEntity.findSingleByAndUpdate(TitanTable.id eq EntityID(callsign, TitanTable) ) {
					when (formChoice) {
						"basic" -> {
							it.name = textInputs["name"]?.value.orEmpty()
							it.chassis = textInputs["chassis"]?.value.orEmpty()
							it.titanClass = textInputs["class"]?.value.orEmpty()
							it.kits = textInputs["kits"]?.value.orEmpty()
						}

						"military" -> {
							it.primaryWeapon = textInputs["primaryweapon"]?.value.orEmpty()
							it.ordinance = textInputs["ordinance"]?.value.orEmpty()
							it.defensive = textInputs["defensive ability"]?.value.orEmpty()
							it.tactical = textInputs["tactical ability"]?.value.orEmpty()
							it.core = textInputs["core"]?.value.orEmpty()
						}

						"lore" -> {
							it.description = textInputs["description"]?.value.orEmpty()
							it.design = textInputs["design"]?.value.orEmpty()
							it.misc = textInputs["misc"]?.value.orEmpty()
						}
					}
				}
			}
	}
)