package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.lang.management.ManagementFactory

@Command(aliases = arrayOf("uptime"), description = "Show the getBot's uptime.")
class UptimeCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val s = ManagementFactory.getRuntimeMXBean().uptime / 1000
        val m = s / 60
        val h = m / 60
        val d = h / 24

        context.send().embed("Bot Uptime") {
            desc { "$d days, ${h % 24} hours, ${m % 60} minutes and ${s % 60} seconds" }
        }.action().queue()
    }
}
