package xyz.gnarbot.gnar

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import xyz.gnarbot.gnar.utils.get
import java.io.File

class Credentials(file: File) {
    val loader: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(file.toPath()).build()

    val config: CommentedConfigurationNode = loader.load()

    val token: String = config["token"].string.takeIf { !it.isNullOrBlank() } ?: error("Bot token can't be null or blank.")

    val abal: String? = config["server counts", "abal"].string
    val carbonitex: String? = config["server counts", "carbonitex"].string
    val discordBots: String? = config["server counts", "discordbots"].string

    val cat: String? = config["cat"].string
    val imgFlip: String? = config["imgflip"].string
    val mashape: String? = config["mashape"].string
}
