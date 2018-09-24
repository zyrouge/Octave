package xyz.gnarbot.gnar.utils

import xyz.gnarbot.gnar.Bot
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class DatabaseManager(val bot: Bot, val mainTable: String) {

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

    fun createTable(tableName: String, options: Array<String>) {
        //CREATE TABLE TABLENAME (OPTIONS)
        executeStatement("CREATE TABLE $tableName VALUES (${options.sortedArray()})")

    }

    fun setGuildData(id: Int, options : HashMap<String, String>) {
        for(s: String in options.keys) {
            //UPDATE TABLE SET KEY = VALUE WHERE ID = ID
            executeStatement("UPDATE $mainTable SET $s = ${options.getValue(s)} WHERE ID = $id")
        }
    }

    fun deleteGuildData(id: Int) {
        //DELETE FROM TABLE WHERE ID = ID
        executeStatement("DELETE FROM $mainTable WHERE ID = $id")
    }

    fun selectGuildData(id: Int) : ResultSet {
        //SELECT * FROM TABLE WHERE ID = ID
        return executeStatement("SELECT * FROM $mainTable WHERE ID = $id")
    }

    fun insertGuildData(id: Int, options : Array<String>) {
        //INSERT INTO TABLE VALUES (KEY, VALUE, ...)
        executeStatement("INSERT INTO $mainTable VALUES ${options.sortedArray()} WHERE ID = $id")
    }

    private fun executeStatement(s: String) : ResultSet {
        val statement : Statement = bot.connection.createStatement()
        statement.executeQuery(s)
        bot.connection.commit()
        return statement.resultSet
    }
}