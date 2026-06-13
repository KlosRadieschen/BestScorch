package database

import Config
import io.github.classgraph.ClassGraph
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object Database {
	private var _db: Database? = null
	private val lock = Any()

	val db: Database
		get() {
			_db?.let { return it }

			try {
				return synchronized(lock) {
					_db ?: connectAndInitialize().also {
						_db = it
					}
				}
			} catch (e: Exception) {
				error("Database error (It's probably just off lmao)")
			}
		}

	private fun connectAndInitialize(): Database {
		val database = Database.connect(
			url = "jdbc:mariadb://${Config.Database.url}:${Config.Database.port}/${Config.Database.schema}",
			driver = "org.mariadb.jdbc.Driver",
			user = Config.Database.username,
			password = Config.Database.password
		)

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

		transaction(database) {
			SchemaUtils.createMissingTablesAndColumns(*tables)
		}

		return database
	}
}