package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("throwError"),
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.send().text(args.contentToString()).queue()
    }
}