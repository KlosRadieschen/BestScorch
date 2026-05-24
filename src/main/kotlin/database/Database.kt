package database

import Config
import io.github.classgraph.ClassGraph
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
	val db = Database.connect(
		url = "jdbc:mariadb://${Config.Database.url}:${Config.Database.port}/${Config.Database.schema}",
		driver = "org.mariadb.jdbc.Driver",
		user = Config.Database.username,
		password = Config.Database.password
	)

	init {
		val tables = ClassGraph()
			.enableClassInfo()
			.acceptPackages("database.tables")
			.scan()
			.use { scanResult ->
				scanResult
					.getSubclasses(Table::class.qualifiedName)
					.loadClasses(Table::class.java)
					.mapNotNull { clazz -> clazz.kotlin.objectInstance }
					.toTypedArray()
			}

		transaction(db) {
			SchemaUtils.createMissingTablesAndColumns(*tables)
		}
	}
}