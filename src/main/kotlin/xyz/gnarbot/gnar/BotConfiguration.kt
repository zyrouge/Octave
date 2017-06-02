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

    val shards: Int = this.config["bot", "shards"].int.takeIf { it != 0 } ?: error("Shard number must be >= 1")

    val name: String = this.config["bot", "name"].string ?: "Gnar"
    val game: String = this.config["bot", "game"].string ?: "%d | _help"
    val avatar: String? = this.config["bot", "avatar"].string
    val consoleChannelID: Long = this.config["bot", "console channel id"].getLong(0)

    val prefix: String = this.config["commands", "prefix"].string ?: "_"

    val administrators: List<Long> = this.config["commands", "administrators"].getList(TypeToken.of(Long::class.javaObjectType))

    val musicEnabled: Boolean = this.config["music", "enabled"].getBoolean(true)
    val queueLimit: Int = this.config["music", "queue limit"].getInt(20)

    val durationLimitText: String = this.config["music", "duration limit"].string
    val durationLimit: Duration = durationLimitText.toDuration()

    val voteSkipCooldownText: String = this.config["music", "vote skip cooldown"].string
    val voteSkipCooldown: Duration = voteSkipCooldownText.toDuration()

    val voteSkipDurationText: String = this.config["music", "vote skip duration"].string
    val voteSkipDuration: Duration = voteSkipDurationText.toDuration()

    val searchDurationText: String = this.config["music", "search duration"].string
    val searchDuration: Duration = searchDurationText.toDuration()

    val accentColor : Color = (this.config["colors", "accent"].string ?: "0050af").let { Color.decode(it) }
    val musicColor: Color = (this.config["colors", "music"].string ?: "00dd58").let { Color.decode(it) }
}
