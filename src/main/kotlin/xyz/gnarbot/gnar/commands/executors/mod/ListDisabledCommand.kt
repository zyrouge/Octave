package xyz.gnarbot.gnar.commands.executors.mod

import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("listdisabled"),
        disableable = false,
        category = Category.MODERATION,
        description = "List disabled commands."
)
class ListDisabledCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.send().embed("Disabled Commands") {
            color = BotConfiguration.ACCENT_COLOR
            description {
                if (context.guildData.commandHandler.disabled.isEmpty()) {
                    "There isn't any command disabled on this server."
                }
                else buildString {
                    context.guildData.commandHandler.disabled.forEach {
                        append("• ")
                        appendln(it.info.aliases.joinToString())
                    }
                }
            }
        }.rest().queue()
    }
}