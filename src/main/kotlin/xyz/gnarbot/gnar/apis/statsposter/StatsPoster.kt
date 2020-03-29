package xyz.gnarbot.gnar.apis.statsposter

import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.apis.statsposter.websites.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatsPoster(botId: String) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    val websites = listOf(
        BotsForDiscord(botId, Bot.getInstance().credentials.botsForDiscord ?: ""),
        BotsGg(botId, Bot.getInstance().credentials.botsGg ?: ""),
        BotsOnDiscord(botId, Bot.getInstance().credentials.botsOnDiscord ?: ""),
        TopGg(botId, Bot.getInstance().credentials.topGg ?: "")
    )

    fun update(count: Long) {
        for (website in websites.filter(Website::canPost)) {
            website.update(count)
                .thenApply { it.body()?.close() }
                .exceptionally {
                    log.error("Updating server count failed for ${website.name}: ", it)
                    return@exceptionally null
                }
        }
    }

    fun postEvery(time: Long, unit: TimeUnit) {
        scheduler.scheduleWithFixedDelay({
            update(Bot.getInstance().shardManager.guildCache.size())
        }, time, time, unit)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StatsPoster::class.java)
    }
}
