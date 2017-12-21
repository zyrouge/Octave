package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.*

@Command(
        aliases = ["sudo"],
        description = "Power."
)
@BotInfo(
        id = 33,
        admin = true,
        category = Category.NONE
)
class SudoCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Put a command pls.").queue()
            return
        }

        val cmd = context.bot.commandRegistry.getCommand(args[0])

        if (cmd == null) {
            context.send().error("Not a valid command.").queue()
            return
        }

        cmd.execute(context, args[0], args.copyOfRange(1, args.size))
    }
}