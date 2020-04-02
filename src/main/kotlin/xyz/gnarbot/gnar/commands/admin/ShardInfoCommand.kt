package xyz.gnarbot.gnar.commands.admin

import com.google.common.collect.Lists
import net.dv8tion.jda.api.JDA
import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["shards", "shard", "shardinfo"],
        description = "Get shard information."
)
@BotInfo(
        id = 48,
        admin = true,
        category = Category.NONE
)
class ShardInfoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().text("```prolog\n ID |    STATUS |    PING | GUILDS |  USERS | REQUESTS |  VC\n```").queue()

        Lists.partition(context.bot.shardManager.shards, 20).forEach {
            val page = it.joinToString("\n", prefix = "```prolog\n", postfix = "```") { shard -> formatInfo(context, shard) }
            context.send().text(page).queue()
        }
    }

    private fun formatInfo(ctx: Context, jda: JDA): String {
        val shardId = jda.shardInfo.shardId
        val totalShards = jda.shardInfo.shardTotal

        return "%3d | %9.9s | %7.7s | %6d | %6d | ---- WIP | %3d".format(
            shardId,
            jda.status,
            "${jda.gatewayPing}ms",
            jda.guildCache.size(),
            jda.userCache.size(),
            ctx.bot.players.registry.values.count { getShardIdForGuild(it.guildId, totalShards) == shardId }
        )
    }

    private fun getShardIdForGuild(guildId: String, shardCount: Int): Int {
        return ((guildId.toLong() shr 22) % shardCount).toInt()
    }

}
