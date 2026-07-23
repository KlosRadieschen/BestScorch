package commands.autoComplete.registry

import commands.autoComplete.AutoComplete
import database.Database
import database.tables.TitanEntity
import database.tables.TitanTable
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.interaction.suggest
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("unused")
object TitanAutocomplete : AutoComplete (
	commandName = "titan",
	run = {
		val titans = transaction(Database.db) {
			TitanEntity.find { TitanTable.ownerID eq user.id.value and (TitanTable.callsign like "${focusedOption.value}%") }.limit(25).toList()
		}

		if (titans.isEmpty()) suggest(choices = listOf())
		else suggest(
			choices = titans.map {
				Choice.StringChoice(
					"${it.callsign} ${it.name ?: ""}",
					value = it.callsign,
					nameLocalizations = Optional.Missing(),
				)
			}
		)
	}
)