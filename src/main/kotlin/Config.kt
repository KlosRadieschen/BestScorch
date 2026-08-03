import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv

object Config {
    private val dotenv = Dotenv.load()

    object Tokens {
        val botToken = dotenv["BOT_TOKEN"]!!
        val mistralToken = dotenv["MISTRAL_TOKEN"]!!
    }

    object Snowflakes {
        val ahaGuildID = Snowflake(dotenv["AHA_GUILD_ID"]!!)

        object Channels {
            val oocCategoryID = Snowflake(dotenv["OOC_CATEGORY_ID"]!!)
            val techCategoryID = Snowflake(dotenv["TECH_CATEGORY_ID"]!!)

            val pollChannelID = Snowflake(dotenv["POLL_CHANNEL_ID"]!!)
            val botChannelID = Snowflake(dotenv["BOT_CHANNEL_ID"]!!)
        }

        object AdminAbusers {
            val adminAbusersImmune = dotenv["ADMIN_ABUSERS_IMMUNE"]?.split(",")?.map { Snowflake(it) }!!
            val adminAbusersNotImmune = dotenv["ADMIN_ABUSERS_NOT_IMMUNE"]?.split(",")?.map { Snowflake(it) }!!
        }

        val executedRoleID = Snowflake(dotenv["EXECUTED_ROLE_ID"]!!)

        suspend fun User.ahaNickname() = this.asMember(ahaGuildID).effectiveName
    }

    object Database {
        val url = dotenv["DB_URL"]!!
        val port = dotenv["DB_PORT"]!!

        val schema = dotenv["MYSQL_DATABASE"]!!
        val username = dotenv["MYSQL_USER"]!!
        val password = dotenv["MYSQL_PASSWORD"]!!
    }
}