package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["patreonUpdate"]
)
@BotInfo(
        id = 120,
        category = Category.NONE,
        admin = true
)
class UpdatePatreonTokenCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        //context.bot.patreon.setAccessToken(args.joinToString(" "))

        context.send().info("Unable to update Patreon token, they updated their API :(").queue()
    }
}
