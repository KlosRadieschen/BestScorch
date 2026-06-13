package commands.autoComplete.registry

import commands.autoComplete.AutoComplete
import database.Database
import database.tables.CharacterEntity
import database.tables.CharacterTable
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.interaction.suggest
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress(names = ["unused"])
object CharacterAutocomplete : AutoComplete (
	commandName = "character",
	run = {
		val characters = transaction(Database.db) {
			CharacterEntity.find { CharacterTable.ownerID eq user.id.value and (CharacterTable.name like "${focusedOption.value}%") }.limit(25).toList()
		}

		if (characters.isEmpty()) suggest(choices = listOf())
		else suggest(
			choices = characters.map {
				Choice.StringChoice(
					it.name,
					value = it.name,
					nameLocalizations = Optional.Missing(),
				)
			}
		)
	}
)