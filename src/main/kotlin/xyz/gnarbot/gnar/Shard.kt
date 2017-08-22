package xyz.gnarbot.gnar

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.hooks.EventListener
import xyz.gnarbot.gnar.utils.CountUpdater
import java.util.concurrent.TimeUnit

//val guildCountListener = GuildCountListener()

class Shard(val id: Int, private vararg val listeners: EventListener) {
    /** @return the amount of successful requests on this command handler. */
    @JvmField var requests = 0

    private val builder = JDABuilder(AccountType.BOT).apply {
        setToken(Bot.KEYS.token)
        if (Bot.KEYS.shards > 1) useSharding(id, Bot.KEYS.shards)
        setAutoReconnect(true)
        setMaxReconnectDelay(32)
        setAudioEnabled(true)
        setAudioSendFactory(NativeAudioSendFactory())
        addEventListener(*listeners)
        setEnableShutdownHook(true)
        setGame(Game.of("Loading..."))
    }

    lateinit var jda: JDA

    private val countUpdater = CountUpdater(this)

    init {
        Bot.EXECUTOR.scheduleAtFixedRate(countUpdater::update, 30, 30, TimeUnit.MINUTES)
    }

    fun build() {
        Bot.LOG.info("Building shard $id.")

        this.jda = builder.buildBlocking().apply {
            selfUser.manager.setName(Bot.CONFIG.name).queue()
        }
    }

    fun buildAsync() {
        Bot.LOG.info("Building shard $id.")

        this.jda = builder.buildAsync()
    }

    fun revive() {
        Bot.LOG.info("Reviving shard $id.")

        jda.removeEventListener(*listeners)
        jda.shutdown()

        build()
    }

    /**
     * @return The string representation of the shard.
     */
    override fun toString() = "Shard(id=$id, guilds=${jda.guilds.size})"
}