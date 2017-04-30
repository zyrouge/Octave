package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.conformToRange
import java.time.OffsetDateTime

@Command(
        aliases = arrayOf("prune", "delmessages", "delmsgs"),
        usage = "-amount -words...", description = "Delete up to 100 messages.",
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = arrayOf(Permission.MESSAGE_MANAGE)
)
class PruneCommand : CommandExecutor() {
    public override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().error("Insufficient amount of arguments.").queue()
            return
        }

        context.message.delete().queue()

        val history = context.send().channel.history

        val amount: Int = (args[0].toIntOrNull() ?: run {
            context.send().error("Improper arguments supplies, must be a number.").queue()
            return
        }).conformToRange(0, 100)

        if (amount < 2) {
            context.send().error("You need to delete 2 or more messages to use this command.").queue()
            return
        }

        history.retrievePast(amount).queue {
            val filters = if (args.size >= 2) {
                args.copyOfRange(1, args.size)
            } else {
                emptyArray()
            }

            val msgList = it.map { msg ->
                when {
                    msg.creationTime.isBefore(OffsetDateTime.now().minusWeeks(2)) -> null
                    filters.isNotEmpty() -> if (args.any { msg.content.contains(it) }) msg else null
                    else -> msg
                }
            }.filterNotNull()

            when {
                msgList.isNotEmpty() -> {
                    context.message.textChannel.deleteMessages(msgList).queue()
                    context.send().info("Attempted to delete **[${it.size}]()** messages.\nDeleting this message in **5** seconds.")
                            .queue(Utils.deleteMessage(5))
                }
                filters.isNotEmpty() -> {
                    context.send().info("No messages (that are younger than 2 weeks) were found with the filters.\nDeleting this message in **5** seconds.")
                            .queue(Utils.deleteMessage(5))
                }
                else -> {
                    context.send().info("No messages were found (that are younger than 2 weeks).\nDeleting this message in **5** seconds.")
                            .queue(Utils.deleteMessage(5))
                }
            }
        }
    }
}
