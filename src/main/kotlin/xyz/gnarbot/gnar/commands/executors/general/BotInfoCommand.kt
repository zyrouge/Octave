package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.JDAInfo
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln
import java.lang.management.ManagementFactory

@Command(
        id = 42,
        aliases = arrayOf("about", "info", "botinfo"),
        description = "Show information about the bot."
)
class BotInfoCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        // Uptime
        val s = ManagementFactory.getRuntimeMXBean().uptime / 1000
        val m = s / 60
        val h = m / 60
        val d = h / 24

        var requests = 0
        var textChannels = 0
        var voiceChannels = 0
        var guilds = 0

        var users = 0

        for (shard in Bot.getShards()) {
            guilds += shard.jda.guilds.size
            requests += shard.requests
            users += shard.jda.users.size
            textChannels += shard.jda.textChannels.size
            voiceChannels += shard.jda.voiceChannels.size
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
                    append("Donations: **[Patreon](https://gnarbot.xyz/donate)**").ln()
                    append("Commands: **$commandSize**").ln()
                    append("Library: **[JDA ${JDAInfo.VERSION}](${JDAInfo.GITHUB})**").ln()
                }
            }
            field("Credits", true) {
                buildString {
                    append("The Serious: **[Avarel](https://github.com/Avarel)**").ln()
                    append("The Sarcastic: **[Xevryll](https://github.com/xevryll)**").ln()
                    append("Contributor: **[Gatt](https://github.com/RealGatt)**").ln()
                }
            }
        }.action().queue()
    }
}
