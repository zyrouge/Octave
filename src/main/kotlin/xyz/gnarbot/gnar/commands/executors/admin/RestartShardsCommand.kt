package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("restartShards"),
        description = "Restart all Shard instances.",
        administrator = true,
        category = Category.NONE
)
class RestartShardsCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Restarting Shards") {
                description { "Bot is now restarting." }
            }.action().queue()

            context.bot.restart()
        } else {
            val id = args[0].toIntOrNull()?.coerceIn(0, context.bot.shards.size) ?: kotlin.run {
                context.send().error("You must enter a valid shard id.").queue()
                return
            }

            context.bot.restart(id)
        }
    }
}