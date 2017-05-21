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
        context.send().embed("Restarting Shards") {
            color = context.bot.config.accentColor
            description = "Bot is now restarting."
        }.action().queue()

        context.bot.restart()
    }
}