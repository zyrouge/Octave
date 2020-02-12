package xyz.gnarbot.gnar

import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import xyz.gnarbot.gnar.utils.get
import java.io.File

class BotCredentials(file: File) {
    private val loader = HoconConfigurationLoader.builder().setFile(file).build()

    private val config = loader.load()

    val token = config["token"].string.takeIf { !it.isNullOrBlank() }
                ?: error("Bot token can't be null or blank.")

    val totalShards = config["sharding", "total"].int.takeIf { it > 0 }
                ?: error("Shard count total needs to be > 0")
    val shardStart = config["sharding", "start"].int.takeIf { it >= 0 }
                ?: error("Shard start needs to be >= 0")
    val shardEnd = config["sharding", "end"].int.takeIf { it in shardStart..totalShards }
                ?: error("Shard end needs to be <= sharding.total")

    val webHookURL: String? = config["webhook url"].string

    val abal: String? = config["server counts", "abal"].string
    val carbonitex: String? = config["server counts", "carbonitex"].string
    val discordBots: String? = config["server counts", "discordbots"].string

    val cat: String? = config["cat"].string
    val imgFlip: String? = config["imgflip"].string
    val mashape: String? = config["mashape"].string

    val malUsername: String? = config["mal credentials", "username"].string
    val malPassword: String? = config["mal credentials", "password"].string

    val weebSh: String? = config["weebsh"].string
    val discordFM: String? = config["discordfm"].string

    val patreonToken: String? = config["patreon", "token"].string

    val riotAPIKey: String? = config["riot", "apiKey"].string
}