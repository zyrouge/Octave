package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("gc"),
        description = "Request Java to garbage collect.",
        administrator = true,
        category = Category.NONE
)
class GarbageCollectCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.send().embed("Garbage Collection") {
            color = BotConfiguration.ACCENT_COLOR
            val interrupt = if (!args.isEmpty()) args[0].toBoolean() else false

            context.bot.shards.forEach { it.clearData(interrupt) }
            field("Wrappers", false, "Removed settings instances.")

            field("Guild Data Remaining", true, context.bot.shards.sumBy { it.guildData.size })

            System.gc()
            field("GC Request", false, "Garbage collection request sent to JVM.")
            context.bot.log.info("Garbage collection request sent to JVM.")
        }.rest().queue()
    }
}