package xyz.gnarbot.gnar

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import xyz.gnarbot.gnar.utils.get
import xyz.gnarbot.gnar.utils.toDuration
import java.io.File
import java.time.Duration
import kotlin.jvm.JvmField as Field


class BotConfiguration(file: File) {
    val loader: ConfigurationLoader<CommentedConfigurationNode> =
            HoconConfigurationLoader.builder().setPath(file.toPath()).build()

    var config: CommentedConfigurationNode = loader.load()

    val name: String = config["bot", "name"].getString("Gnar")
    val game: String = config["bot", "game"].getString("_help | %d")
    val avatar: String? = config["bot", "avatar"].string

    val prefix: String = config["commands", "prefix"].getString("_")

    val admins: List<Long> = config["commands", "administrators"].getList(TypeToken.of(Long::class.javaObjectType))

    val musicEnabled: Boolean = config["music", "enabled"].getBoolean(true)
    val queueLimit: Int = config["music", "queue limit"].getInt(20)
    val musicLimit: Int = config["music", "limit"].getInt(500)

    val durationLimitText: String = config["music", "duration limit"].getString("2 hours")
    val durationLimit: Duration = durationLimitText.toDuration()

    val voteSkipCooldownText: String = config["music", "vote skip cooldown"].getString("35 seconds")
    val voteSkipCooldown: Duration = voteSkipCooldownText.toDuration()

    val voteSkipDurationText: String = config["music", "vote skip duration"].getString("20 seconds")
    val voteSkipDuration: Duration = voteSkipDurationText.toDuration()
}
