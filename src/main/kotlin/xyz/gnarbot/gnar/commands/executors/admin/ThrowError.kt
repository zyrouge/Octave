package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = ["throwError"]
)
@BotInfo(
        id = 38,
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val cmds = context.bot.commandRegistry.entries
        context.send().text(
                Utils.hasteBin(
                        buildString {
                            cmds.forEach {
                                append(it.info.aliases.contentToString()).append(' ').append(it.info.usage).append('\n')
                                append(" - ").append(it.info.description).append("\n\n")
                            }
                        }
                ) ?: "Can not post to HasteBin."
        ).queue()
    }
}