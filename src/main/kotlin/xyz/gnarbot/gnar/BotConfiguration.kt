package xyz.gnarbot.gnar

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import xyz.gnarbot.gnar.utils.get
import xyz.gnarbot.gnar.utils.toDuration
import java.awt.Color
import java.io.File
import java.time.Duration
import kotlin.jvm.JvmField as Field


class BotConfiguration(file: File) {
    var loader: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(file.toPath()).build() //File(DATA_FOLDER, "bot.conf")

    var config: CommentedConfigurationNode = loader.load()

    val shards: Int = config["bot", "shards"].int.takeIf { it != 0 } ?: error("Shard number must be >= 1")

    val name: String = config["bot", "name"].getString("Gnar")
    val game: String = config["bot", "game"].getString("%d | _help")
    val avatar: String? = config["bot", "avatar"].string
    val consoleChannelID: Long = config["bot", "console channel id"].getLong(0)

    val prefix: String = config["commands", "prefix"].getString("_")

    val administrators: List<Long> = config["commands", "administrators"].getList(TypeToken.of(Long::class.javaObjectType))

    val musicEnabled: Boolean = config["music", "enabled"].getBoolean(true)
    val queueLimit: Int = config["music", "queue limit"].getInt(20)

    val durationLimitText: String = config["music", "duration limit"].getString("2 hours")
    val durationLimit: Duration = durationLimitText.toDuration()

    val voteSkipCooldownText: String = config["music", "vote skip cooldown"].getString("35 seconds")
    val voteSkipCooldown: Duration = voteSkipCooldownText.toDuration()

    val voteSkipDurationText: String = config["music", "vote skip duration"].getString("20 seconds")
    val voteSkipDuration: Duration = voteSkipDurationText.toDuration()

    val searchDurationText: String = config["music", "search duration"].getString("2 minutes")
    val searchDuration: Duration = searchDurationText.toDuration()

    val accentColor : Color = config["colors", "accent"].getString("0x0050af").let { Color.decode(it) }
    val musicColor: Color = config["colors", "music"].getString("0x00dd58").let { Color.decode(it) }
    val errorColor: Color = config["colors", "error"].getString("0xFF0000").let { Color.decode(it) }
}
