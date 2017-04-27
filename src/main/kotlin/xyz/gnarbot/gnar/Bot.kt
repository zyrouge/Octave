package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.api.data.BotInfo
import xyz.gnarbot.gnar.commands.CommandRegistry
import xyz.gnarbot.gnar.listeners.GuildCountListener
import xyz.gnarbot.gnar.listeners.UserListener
import kotlin.jvm.JvmStatic as static

/**
 * Main class of the bot. Implemented as a singleton.
 *
 * @param token Discord token.
 * @param numShards Number of shards to request.
 */
class Bot(val token: String, val numShards: Int) {
    /** @returns The logger instance of the bot. */
    val log: Logger = LoggerFactory.getLogger("Bot")

    val commandRegistry = CommandRegistry()

    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().apply {
        registerSourceManager(YoutubeAudioSourceManager())
        registerSourceManager(SoundCloudAudioSourceManager())
        registerSourceManager(VimeoAudioSourceManager())
        registerSourceManager(BandcampAudioSourceManager())
        registerSourceManager(TwitchStreamAudioSourceManager())
        registerSourceManager(BeamAudioSourceManager())
    }

    /** @return Sharded JDA instances of the bot.*/
    val shards = mutableListOf<Shard>()

    private val guildCountListener = GuildCountListener(this)
    private val userListener = UserListener()

    /** @return the amount of successful requests on this command handler. */
    var requests = 0

    /** Start the bot. */
    fun start() {
        check(!token.isNullOrEmpty()) { "Bot token can not be null." }

        log.info("Initializing the Discord bot.")

        log.info("Name:\t${BotConfiguration.BOT_NAME}")
        log.info("Shards:\t$numShards")
        log.info("Prefix:\t${BotConfiguration.PREFIX}")
        log.info("Admins:\t${BotConfiguration.ADMINISTRATORS.size}")

        for (id in 0 until numShards) {
            val jda = with (JDABuilder(AccountType.BOT)) {
                setToken(token)
                setAutoReconnect(true)
                addEventListener(guildCountListener)
                addEventListener(userListener)
                setAudioSendFactory(NativeAudioSendFactory())
                setGame(Game.of(BotConfiguration.BOT_GAME.format(id)))
                setAudioEnabled(true)
            }.buildBlocking()

            log.info("JDA $id is ready.")

            jda.selfUser.manager.setName(BotConfiguration.BOT_NAME).queue()

            shards += Shard(id, jda, this)
        }

        log.info("The bot is now fully connected to Discord.")
    }

    fun restart(id : Int) {
        log.info("Restarting the Discord bot shard $id.")

        shards[id].shutdown()

        val jda = with (JDABuilder(AccountType.BOT)) {
            setToken(token)
            setAutoReconnect(true)
            addEventListener(guildCountListener)
            addEventListener(userListener)
            setAudioSendFactory(NativeAudioSendFactory())
            setGame(Game.of(BotConfiguration.BOT_GAME.format(id)))
            setAudioEnabled(true)
        }.buildBlocking()

        log.info("JDA $id has restarted.")

        jda.selfUser.manager.setName("Gnarr").queue()

        shards[id] = Shard(id, jda, this)
    }

    fun restart() {
        log.info("Restarting the Discord bot shards.")

        (0 until shards.size).forEach(this::restart)

        log.info("Discord bot shards have now restarted.")
    }

    /**
     * Stop the bot.
     */
    fun stop() {
        shards.forEach(Shard::shutdown)

        log.info("Bot is now disconnected from Discord.")
    }

    val info: BotInfo get() = BotInfo(this)
}