package xyz.gnarbot.gnar.commands.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.template.parser.Parsers
import java.lang.NumberFormatException
import java.time.Duration

@Command(
        aliases = ["songlength"],
        usage = "30 minutes 20 seconds",
        description = "Sets the maximum song length allowed."
)
@BotInfo(
        id = 9348,
        category = Category.SETTINGS,
        scope = Scope.TEXT,
        permissions = [Permission.MANAGE_SERVER]
)
class SongLengthCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<out String>) {
        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        if (args[0] == "reset") {
            context.data.music.maxSongLength = 0
            context.data.save()

            context.send().info("Reset song length limit.")
            return
        }

        val amount: Duration? = Parsers.DURATION.parse(context, args[0])

        if(amount == null) {
            context.send().info("Wrong duration specified: Expected something like `2 hours 40 minutes`")
            return
        }

        if(amount > config.durationLimit) {
            context.send().error("This is too much. The limit is ${config.durationLimitText}.")
            return
        }

        context.data.music.maxSongLength = amount.toMillis()
        context.data.save()
        context.send().info("Successfully set song length limit to ${args[0]}.")
    }
}