package xyz.gnarbot.gnar.utils

import io.sentry.Sentry
import xyz.gnarbot.gnar.Bot
import java.sql.Connection
import java.sql.DriverManager
import kotlin.system.exitProcess

class DatabaseManager(val bot: Bot, private val mainTable: String) {

    fun establishConnection(): Connection? {
        return try {
            Class.forName("org.postgresql.Driver")
            DriverManager.getConnection(bot.credentials.databaseURL, bot.credentials.databaseUsername, bot.credentials.databasePassword)
        } catch (e: Exception) {
            Sentry.capture(e)
            e.printStackTrace()
            exitProcess(0)
        }
    }

}
