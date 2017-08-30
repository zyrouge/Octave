package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.RateLimiter
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@Command(
        id = 38,
        aliases = arrayOf("throwError"),
        admin = true,
        category = Category.NONE
)
class ThrowError : CommandExecutor() {
    private val rateLimiter = RateLimiter<Long>(2, 5, TimeUnit.SECONDS)

    override fun execute(context: Context, label: String, args: Array<String>) {
//        if (rateLimiter.check(context.user.idLong)) {
//            context.send().text("Not rate-limited!").queue()
//        } else {
//            context.send().text("Rate-limited! Try again in ${Utils.getTime(rateLimiter.remainingTime(context.user.idLong))}").queue()
//        }

        context.send().info(buildString {
            var x = 0

            val ms = measureTimeMillis {
                x = Bot.getUserCount()
            }

            append(ms).append("ms")
            append(x).append(" unique users")
        }).queue()
    }
}