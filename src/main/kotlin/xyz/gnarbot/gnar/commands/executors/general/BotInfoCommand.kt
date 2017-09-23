package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.JDAInfo
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.lang.management.ManagementFactory

@Command(
        id = 42,
        aliases = arrayOf("about", "info", "botinfo"),
        description = "Show information about the bot."
)
class BotInfoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        // Uptime
        val s = ManagementFactory.getRuntimeMXBean().uptime / 1000
        val m = s / 60
        val h = m / 60
        val d = h / 24

        var requests = 0L
        var textChannels = 0L
        var voiceChannels = 0L
        var guilds = 0L

        var users = 0L

        for (shard in Bot.getShards()) {
            guilds += shard.jda.guildCache.size()
            requests += shard.requests
            users += shard.jda.userCache.size()
            textChannels += shard.jda.textChannelCache.size()
            voiceChannels += shard.jda.voiceChannelCache.size()
        }

        val commandSize = registry.entries.count { it.info.category.show }

        context.send().embed("Bot Information") {
            thumbnail { context.jda.selfUser.avatarUrl }
            desc {
                "Gnar is a music bot packed with dank memes to rescue your soul from the depths of the underworld."
            }

            field("Session Requests", true) { requests }
            field("Requests Per Hour", true) { requests / Math.max(1, h) }

            field("Text Channels", true) { textChannels }
            field("Voice Channels", true) { voiceChannels }

            field("Guilds", true) { guilds }
            field("Voice Connections", true) { Bot.getPlayers().size() }

            field("Users", true) { users }
            field("Uptime", true) { "${d}d ${h % 24}h ${m % 60}m ${s % 60}s" }

            field("General", true) {
                buildString {
                    append("Donations: **[Patreon](https://gnarbot.xyz/donate)**\n")
                    append("Commands: **$commandSize**\n")
                    append("Library: **[JDA ${JDAInfo.VERSION}](${JDAInfo.GITHUB})**\n")
                }
            }
            field("Credits", true) {
                buildString {
                    append("The Serious: **[Avarel](https://github.com/Avarel)**\n")
                    append("The Sarcastic: **[Xevryll](https://github.com/xevryll)**\n")
                    append("Contributor: **[Gatt](https://github.com/RealGatt)**\n")
                }
            }
        }.action().queue()
    }
}
