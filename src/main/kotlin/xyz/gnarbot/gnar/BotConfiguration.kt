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
    val loader: ConfigurationLoader<CommentedConfigurationNode> =
            HoconConfigurationLoader.builder().setPath(file.toPath()).build()

    var config: CommentedConfigurationNode

    var name: String
    var game: String
    var avatar: String
    var consoleChannelID: Long

    var prefix: String

    var admins: List<Long>
    var donors: List<Long>

    var musicEnabled: Boolean
    var queueLimit: Int

    var durationLimitText: String
    var durationLimit: Duration

    var voteSkipCooldownText: String
    var voteSkipCooldown: Duration

    var voteSkipDurationText: String
    var voteSkipDuration: Duration

    var searchDurationText: String
    var searchDuration: Duration

    var accentColor : Color
    var musicColor: Color
    var errorColor: Color

    init {
        config = loader.load()

        name = config["bot", "name"].getString("Gnar")
        game = config["bot", "game"].getString("%d | _help")
        avatar = config["bot", "avatar"].string
        consoleChannelID = config["bot", "console channel id"].getLong(0)

        prefix = config["commands", "prefix"].getString("_")
        admins = config["commands", "administrators"].getList(TypeToken.of(Long::class.javaObjectType))
        donors = config["commands", "donors"].getList(TypeToken.of(Long::class.javaObjectType))

        musicEnabled = config["music", "enabled"].getBoolean(true)
        queueLimit = config["music", "queue limit"].getInt(20)

        durationLimitText = config["music", "duration limit"].getString("2 hours")
        durationLimit = durationLimitText.toDuration()

        voteSkipCooldownText = config["music", "vote skip cooldown"].getString("35 seconds")
        voteSkipCooldown = voteSkipCooldownText.toDuration()

        voteSkipDurationText = config["music", "vote skip duration"].getString("20 seconds")
        voteSkipDuration = voteSkipDurationText.toDuration()

        searchDurationText = config["music", "search duration"].getString("2 minutes")
        searchDuration = searchDurationText.toDuration()

        accentColor = config["colors", "accent"].getString("0x0050af").let { Color.decode(it) }
        musicColor = config["colors", "music"].getString("0x00dd58").let { Color.decode(it) }
        errorColor = config["colors", "error"].getString("0xFF0000").let { Color.decode(it) }
    }

    fun reload() {
        config = loader.load()

        name = config["bot", "name"].getString("Gnar")
        game = config["bot", "game"].getString("%d | _help")
        avatar = config["bot", "avatar"].string
        consoleChannelID = config["bot", "console channel id"].getLong(0)

        prefix = config["commands", "prefix"].getString("_")
        admins = config["commands", "administrators"].getList(TypeToken.of(Long::class.javaObjectType))
        donors = config["commands", "donors"].getList(TypeToken.of(Long::class.javaObjectType))

        musicEnabled = config["music", "enabled"].getBoolean(true)
        queueLimit = config["music", "queue limit"].getInt(20)

        durationLimitText = config["music", "duration limit"].getString("2 hours")
        durationLimit = durationLimitText.toDuration()

        voteSkipCooldownText = config["music", "vote skip cooldown"].getString("35 seconds")
        voteSkipCooldown = voteSkipCooldownText.toDuration()

        voteSkipDurationText = config["music", "vote skip duration"].getString("20 seconds")
        voteSkipDuration = voteSkipDurationText.toDuration()

        searchDurationText = config["music", "search duration"].getString("2 minutes")
        searchDuration = searchDurationText.toDuration()

        accentColor = config["colors", "accent"].getString("0x0050af").let { Color.decode(it) }
        musicColor = config["colors", "music"].getString("0x00dd58").let { Color.decode(it) }
        errorColor = config["colors", "error"].getString("0xFF0000").let { Color.decode(it) }
    }
}
