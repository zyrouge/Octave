package xyz.gnarbot.gnar.commands.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*
import java.lang.NumberFormatException

@Command(
        aliases = ["queuesize", "qs"],
        usage = "100",
        description = "Sets the maximum queue size."
)
@BotInfo(
        id = 577,
        category = Category.SETTINGS,
        scope = Scope.TEXT,
        permissions = [Permission.MANAGE_SERVER]
)
class QueueSizeCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<out String>) {
        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        if (args[0] == "reset") {
            context.data.music.maxQueueSize = 0
            context.data.save()

            context.send().info("Reset queue limit.")
            return
        }

        val amount: Int = try {
            args[0].toInt()
        } catch (e: NumberFormatException) {
            context.send().error("You need to input a number from 1 to ${config.queueLimit}.")
            return
        }

        if(amount > config.queueLimit) {
            context.send().error("This is too much. The limit is ${config.queueLimit}.")
            return
        }

        context.data.music.maxQueueSize = amount
        context.data.save()
        context.send().info("Successfully set queue limit to $amount.")
    }
}