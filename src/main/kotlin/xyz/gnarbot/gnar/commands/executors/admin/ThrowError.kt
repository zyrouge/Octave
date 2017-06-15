package xyz.gnarbot.gnar.commands.executors.admin

import com.jagrosh.jdautilities.menu.PaginatorBuilder
import xyz.gnarbot.gnar.Bot
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
        PaginatorBuilder(Bot.getWaiter()).apply {
            for (i in 1..100) {
                add("Item $i")
            }
        }.build().display(context.channel)
//        throw RuntimeException("Requested to throw an error, so here you go.")
    }
}