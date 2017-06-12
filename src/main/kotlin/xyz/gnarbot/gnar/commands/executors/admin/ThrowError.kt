package xyz.gnarbot.gnar.commands.executors.admin

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.util.concurrent.TimeUnit

@Command(
        aliases = arrayOf("throwError"),
        administrator = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.bot.waiter.waitFor(GuildMessageReceivedEvent::class.java) {
            context.send().text("You said: ${it.message.content}").queue()
        }.predicate {
            it.member == context.member
        }.timeout(2, TimeUnit.SECONDS) {
            context.send().text("Timed out!").queue()
        }


//        throw RuntimeException("Requested to throw an error, so here you go.")
    }
}