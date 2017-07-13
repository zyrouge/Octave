package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln

@Command(
        id = 38,
        aliases = arrayOf("throwError"),
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val cmds = Bot.getCommandRegistry().entries
        context.send().text(
                Utils.hasteBin(
                        buildString {
                            cmds.forEach {
                                append(it.info.aliases.contentToString()).append(' ').append(it.info.usage).ln()
                                append(" - ").append(it.info.description).ln().ln()
                            }
                        }
                )
        ).queue()
    }
}