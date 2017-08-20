package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

@Command(
        id = 38,
        aliases = arrayOf("throwError"),
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val cmds = Bot.getCommandRegistry().entries
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