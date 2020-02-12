package xyz.gnarbot.gnar.utils

import xyz.gnarbot.gnar.Bot
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class DatabaseManager(val bot: Bot, private val mainTable: String) {

    fun establishConnection() : Connection? {
        var c : Connection? = null
        try {
            Class.forName("org.postgresql.Driver")
            c = DriverManager.getConnection(bot.credentials.databaseURL, bot.credentials.databaseUsername, bot.credentials.databasePassword)
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(0)
        }
        if (c == null) {
            System.exit(0)
        } else {
            return c
        }
        return null
    }
}