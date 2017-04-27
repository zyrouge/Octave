package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("disable"),
        usage = "[labels...]",
        description = "Disable commands.",
        disableable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class DisableCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val disabled = args
                .map(context.guildData.commandHandler::disableCommand)
                .filterNotNull()
                .map { it.info.aliases[0] }

        context.send().embed("Disabling Commands") {
            color = BotConfiguration.ACCENT_COLOR
            description {
                if (disabled.isNotEmpty()) {
                    "Disabled `$disabled` command(s) on this server."
                } else {
                    "You didn't enter any enabled commands or commands that could be disabled."
                }
            }
        }.rest().queue()
    }
}