package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.IEventManager
import net.dv8tion.jda.core.requests.SessionReconnectQueue
import xyz.gnarbot.gnar.utils.CountUpdater
import java.util.concurrent.TimeUnit

//val guildCountListener = GuildCountListener()

class Shard(val id: Int, srq: SessionReconnectQueue, eventManager: IEventManager?, private vararg val listeners: EventListener) {
    /** @return the amount of successful requests on this command handler. */
    @JvmField var requests = 0

    private val builder = JDABuilder(AccountType.BOT).apply {
        setToken(Bot.KEYS.token)
        if (Bot.KEYS.botShards > 1) useSharding(id, Bot.KEYS.botShards)
        setAutoReconnect(true)
        setMaxReconnectDelay(32)
        setAudioEnabled(true)
        setReconnectQueue(srq)
        setAudioSendFactory(NativeAudioSendFactory())
        setEventManager(eventManager)
        addEventListener(*listeners)
        setEnableShutdownHook(true)
        setGame(Game.of("Loading..."))
    }

    lateinit var jda: JDA

    private val countUpdater = CountUpdater(this)

    init {
        Bot.EXECUTOR.scheduleAtFixedRate({ countUpdater.update() }, 30, 30, TimeUnit.MINUTES)
    }

    fun build() {
        Bot.LOG.info("Building shard $id.")

        this.jda = builder.buildBlocking().apply {
            selfUser.manager.setName(Bot.CONFIG.name).queue()
        }
    }

    fun buildAsync() {
        Bot.LOG.info("Building shard $id.")

        this.jda = builder.buildAsync().apply {
            launch(CommonPool) {
                while (status != JDA.Status.CONNECTED) {
                    delay(50)
                }
                selfUser.manager.setName(Bot.CONFIG.name).queue()
            }
        }
    }

    fun revive() {
        Bot.LOG.info("Reviving shard $id.")

        jda.removeEventListener(*listeners)
        jda.shutdown()

        build()

        jda.presence.game = Game.of(Bot.CONFIG.game.format(id))
    }

    /**
     * @return The string representation of the shard.
     */
    override fun toString() = "Shard(id=$id, guilds=${jda.guilds.size})"
}