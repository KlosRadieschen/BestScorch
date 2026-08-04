package commands.helpers

import commands.helpers.Execution.isExecuted
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.rest.NamedFile
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import java.io.File
import kotlin.random.Random

object Vibechecker {
	private val awaitedUsers = mutableSetOf<Snowflake>()

	private fun resourceImage(name: String, path: String): NamedFile =
		NamedFile(name, ChannelProvider { File("src/main/resources/$path").readChannel() })

	private val requestImages = listOf(
		resourceImage("request.png", "request1.png"),
		resourceImage("request.png", "request2.PNG")
	)

	val investigationImages = listOf(
		resourceImage("investigation.jpg", "investigation1.JPG"),
		resourceImage("investigation.jpg", "investigation2.jpg")
	)

	private val successImages = listOf(
		resourceImage("passed.jpg", "passed1.JPG"),
		resourceImage("passed.jpg", "passed2.jpg"),
		resourceImage("passed.jpg", "passed3.jpg")
	)

	private val failureImages = listOf(
		resourceImage("failed.jpg", "failed1.JPG"),
		resourceImage("failed.jpg", "failed2.JPG"),
		resourceImage("failed.jpg", "failed3.jpg")
	)

	fun User.awaitVibe(): NamedFile {
		awaitedUsers.add(id)
		return requestImages.random()
	}

	fun User.isVibeAwaited(): Boolean = id in awaitedUsers && !isExecuted()

	fun User.checkVibe(): NamedFile {
		awaitedUsers.remove(id)
		return if (Random.nextBoolean()) successImages.random() else failureImages.random()
	}
}