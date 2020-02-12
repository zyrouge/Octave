package xyz.gnarbot.gnar.commands.general

import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import java.lang.management.ManagementFactory

@Command(
        aliases = ["uptime"],
        description = "Show the bot's uptime."
)
@BotInfo(
        id = 49
)
class UptimeCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val s = ManagementFactory.getRuntimeMXBean().uptime / 1000
        val m = s / 60
        val h = m / 60
        val d = h / 24

        context.send().info("$d days, ${h % 24} hours, ${m % 60} minutes and ${s % 60} seconds").queue()
    }
}
