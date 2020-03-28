package xyz.gnarbot.gnar.commands.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.template.parser.Parsers
import java.time.Duration

@Command(
        aliases = ["voteskipduration"],
        usage = "30 minutes 20 seconds",
        description = "Sets the voteskip cooldown."
)
@BotInfo(
        id = 9342,
        category = Category.SETTINGS,
        scope = Scope.TEXT,
        permissions = [Permission.MANAGE_SERVER]
)
class VoteSkipDurationCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<out String>) {
        if (args.isEmpty()) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        if (args[0] == "reset") {
            context.data.music.voteSkipDuration = 0
            context.data.save()

            context.send().info("Reset voteskip cooldown.")
            return
        }

        val amount: Duration? = Parsers.DURATION.parse(context, args[0])

        if(amount == null) {
            context.send().info("Wrong duration specified: Expected something like `2 hours 40 minutes`")
            return
        }

        if(amount > config.voteSkipDuration) {
            context.send().error("This is too much. The limit is ${config.voteSkipDurationText}.")
            return
        }

        context.data.music.voteSkipDuration = amount.toMillis()
        context.data.save()
        context.send().info("Successfully set vote skip duration to ${args[0]}.")
    }
}