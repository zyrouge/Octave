package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("shards", "shard", "shardinfo"),
        description = "Get shard information.",
        category = Category.NONE
)
class ShardInfoCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        var page = if (args.isNotEmpty()) {
            args[0].toIntOrNull() ?: 1
        } else {
            1
        }

        context.send().embed("Shard Information") {
            color = BotConfiguration.ACCENT_COLOR

            val pages = Lists.partition(context.bot.shards.toList(), 12)

            if (page >= pages.size) page = pages.size
            else if (page <= 0) page = 1

            val shards = pages[page - 1]

            shards.forEach {
                field("Shard ${it.shardInfo.shardId}", true) {
                    buildString {
                        append("Status: ").appendln(it.status)
                        append("Guilds: ").appendln(it.guilds.size)
                        append("Users: ").appendln(it.users.size)
                        append("Requests: ").appendln(it.guildData.values.sumBy { it.commandHandler.requests })
                    }
                }
            }

            footer = "Page [$page/${pages.size}]"
        }.rest().queue()
    }
}