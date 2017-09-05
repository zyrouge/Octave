package xyz.gnarbot.gnar.commands.executors.admin

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.JDA
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 37,
        aliases = arrayOf("revive", "restartShards"),
        description = "Restart all shard instances.",
        admin = true,
        category = Category.NONE
)
class RestartShardsCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Try `all, dead, (#)`").queue()
            return
        }

        when(args[0]) {
            "all" -> {
                context.send().embed("Restarting Shards") {
                    desc { "Bot is now restarting." }
                }.action().queue()

                launch(CommonPool) {
                    for (shard in Bot.getShards()) {
                        shard.revive()
                        delay(5000)
                    }
                }
            }
            "dead" -> {
                val deadShards = Bot.getShards().filter { it.jda.status != JDA.Status.CONNECTED }

                if (deadShards.isEmpty()) {
                    context.send().info("Every shard is connected.").queue()
                    return
                }

                context.send().embed("Restarting Shards") {
                    desc {
                        "Shards `${deadShards.map(Shard::id)}` are now restarting."
                    }
                }.action().queue()

                deadShards.forEach(Shard::revive)
            }
            else -> {
                val id = args[0].toIntOrNull()?.coerceIn(0, Bot.getShards().size) ?: kotlin.run {
                    context.send().error("You must enter a valid shard id.").queue()
                    return
                }

                context.send().embed("Restarting Shards") {
                    desc {
                        "Shards `$id` are now restarting."
                    }
                }.action().queue()

                Bot.getShard(id).revive()
            }
        }
    }
}