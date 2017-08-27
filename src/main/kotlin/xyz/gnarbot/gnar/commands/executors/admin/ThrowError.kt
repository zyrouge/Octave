package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.RateLimiter
import xyz.gnarbot.gnar.utils.Utils
import java.util.concurrent.TimeUnit

@Command(
        id = 38,
        aliases = arrayOf("throwError"),
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    private val ratelimiter = RateLimiter(2, 5, TimeUnit.SECONDS)

    override fun execute(context: Context, label: String, args: Array<String>) {
        if (ratelimiter.check(context.user.idLong)) {
            context.send().text("Not rate-limited!").queue()
        } else {
            context.send().text("Rate-limited! Try again in ${Utils.getTime(ratelimiter.remainingTime(context.user.idLong))}").queue()
        }
//        val cmds = Bot.getCommandRegistry().entries
//        context.send().text(
//                Utils.hasteBin(
//                        buildString {
//                            cmds.forEach {
//                                append(it.info.aliases.contentToString()).append(' ').append(it.info.usage).append('\n')
//                                append(" - ").append(it.info.description).append("\n\n")
//                            }
//                        }
//                ) ?: "Can not post to HasteBin."
//        ).queue()
    }
}