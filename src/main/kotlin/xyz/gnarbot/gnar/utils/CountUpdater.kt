package xyz.gnarbot.gnar.utils

import com.github.natanbc.discordbotsapi.DiscordBotsAPI
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.*
import org.json.JSONObject
import xyz.gnarbot.gnar.Bot
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CountUpdater(private val bot: Bot) {
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    var discordBotsAPI = DiscordBotsAPI.Builder()
            .setToken(bot.credentials.discordBots)
            .build();

    init {
        executor.scheduleAtFixedRate({
            update(bot.shardManager)
        }, 30, 30, TimeUnit.MINUTES)
    }

    private fun update(shardManager: ShardManager) {
        Bot.getLogger().info("Sending shard updates")
        updateGuildCount(shardManager)
    }

    private fun updateGuildCount(shardManager: ShardManager) {
        val count = shardManager.guildCache.size().toInt();
        discordBotsAPI.postStats(count).execute();
        Bot.getLogger().info("Updated count, current: $count")
    }
}