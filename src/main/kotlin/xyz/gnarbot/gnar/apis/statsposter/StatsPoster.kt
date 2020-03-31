package xyz.gnarbot.gnar.apis.statsposter

import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.apis.statsposter.websites.TopGg
import xyz.gnarbot.gnar.apis.statsposter.websites.Website
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatsPoster(botId: String) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val websites = listOf(
        TopGg(botId, Bot.getInstance().credentials.discordBots ?: "")
    )

    fun update(count: Long) {
        for (website in websites.filter(Website::canPost)) {
            website.update(count)
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
