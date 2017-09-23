package xyz.gnarbot.gnar.commands.executors.admin

import com.google.common.collect.Lists
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.util.*

@Command(
        id = 48,
        aliases = arrayOf("shards", "shard", "shardinfo"),
        description = "Get shard information.",
        admin = true,
        category = Category.NONE
)
class ShardInfoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().text("```prolog\n ID |    STATUS |    PING | GUILDS |  USERS | REQUESTS |  VC\n```").queue()

        Lists.partition(Bot.getShards().toList(), 20).forEach {
            val joiner = StringJoiner("\n", "```prolog\n", "```")

            it.forEach {
                joiner.add(
                        "%3d | %9.9s | %7.7s | %6d | %6d | %8d | %3d".format(
                                it.id,
                                it.jda.status,
                                "${it.jda.ping}ms",
                                it.jda.guildCache.size(),
                                it.jda.userCache.size(),
                                it.requests,
                                Bot.getPlayers().registry.values.count { m -> m.guild.jda == it.jda }
                        )
                )
            }

            context.send().text(joiner.toString()).queue()
        }
    }

}