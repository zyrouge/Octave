package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.entities.Game
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.api.data.BotInfo
import xyz.gnarbot.gnar.commands.CommandRegistry
import xyz.gnarbot.gnar.listeners.GuildCountListener
import xyz.gnarbot.gnar.listeners.UserListener
import xyz.gnarbot.gnar.utils.DiscordLogBack
import xyz.gnarbot.gnar.utils.SimpleLogToSLF4JAdapter
import kotlin.jvm.JvmStatic as static

/**
 * Main class of the bot. Implemented as a singleton.
 *
 * @param token Discord token.
 * @param numShards Number of shards to request.
 */
class Bot(val config: BotConfiguration, val keys: Credentials) {
    private val guildCountListener = GuildCountListener(this)
    private val userListener = UserListener()

    /** @return Sharded JDA instances of the bot.*/
    val shards : Array<Shard>

    /** @returns The logger instance of the bot. */
    val log: Logger = LoggerFactory.getLogger("Bot")

    val commandRegistry = CommandRegistry(this)

    init {
        //SimpleLog.addFileLogs(File("bot.stdout.log"), File("bot.err.log"))
        SimpleLogToSLF4JAdapter.install()
        DiscordLogBack.enable(this)

        check(!keys.token.isNullOrEmpty()) { "Bot token can not be null." }

        log.info("Initializing the Discord bot.")

        log.info("Name:\t${config.name}")
        log.info("Shards:\t${config.shards}")
        log.info("Prefix:\t${config.prefix}")
        log.info("Admins:\t${config.administrators.size}")
        log.info("JDA:\t\t${JDAInfo.VERSION}")

        shards = Array(config.shards, this::createShard)

        log.info("The bot is now fully connected to Discord.")
    }

    private fun createShard(id: Int) : Shard {
        val jda = with (JDABuilder(AccountType.BOT)) {
            setToken(keys.token)
            if (config.shards > 1) useSharding(id, config.shards)
            setAutoReconnect(true)
            addEventListener(guildCountListener)
            addEventListener(userListener)
            setAudioSendFactory(NativeAudioSendFactory())
            setGame(Game.of(config.game.format(id)))
            setAudioEnabled(true)
        }.buildBlocking()

        jda.selfUser.manager.setName(config.name).queue()

        log.info("JDA $id is ready.")

        return Shard(id, jda, this)
    }

    fun restart(id : Int) {
        log.info("Restarting the Discord bot shard $id.")

        shards[id].shutdown()

        shards[id] = createShard(id)
    }

    fun restart() {
        log.info("Restarting the Discord bot shards.")

        shards.map(Shard::id).forEach(this::restart)

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