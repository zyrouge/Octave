package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.jda
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

    /** Start the bot. */
    fun start() {
        log.info("Initializing the Discord bot.")

        log.info("Name:\t${Constants.BOT_NAME}")
        log.info("Shards:\t$numShards")
        log.info("Prefix:\t${Constants.PREFIX}")
        log.info("Admins:\t${Constants.ADMINISTRATORS.size}")
        log.info("Blocked:\t${Constants.BLOCKED_USERS.size}")

        val guildCountListener = GuildCountListener(this)
        val userListener = UserListener()

        for (id in 0 until numShards) {
            val jda = jda(token, id, numShards) {
                setToken(token)
                setAutoReconnect(true)
                addEventListener(guildCountListener)
                addEventListener(userListener)
                setGame(Game.of(Constants.BOT_GAME.format(id)))
                setAudioEnabled(true)
            }



            log.info("JDA $id is ready.")

            jda.selfUser.manager.setName(Constants.BOT_NAME).queue()

            shards += Shard(id, jda, this)
        }

        log.info("The bot is now fully connected to Discord.")
    }

    fun restart() {
        log.info("Restarting the Discord bot shards.")

        for (id in 0 until shards.size) {
            shards[id].shutdown()

            val jda = jda(token, id, numShards) {
                setToken(token)
                setAutoReconnect(true)
                setGame(Game.of("$id | _help"))
                setAudioEnabled(true)
            }

            log.info("JDA $id has restarted.")

            jda.selfUser.manager.setName("Gnarr").queue()

            shards[id] = Shard(id, jda, this)
        }

        log.info("Discord bot shards have now restarted.")
    }

    /**
     * Stop the bot.
     */
    fun stop() {
        shards.forEach(Shard::shutdown)
        System.gc()

        log.info("Bot is now disconnected from Discord.")
    }

    val info: BotInfo get() = BotInfo(this)
}