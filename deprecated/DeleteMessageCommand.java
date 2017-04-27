package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Scope;
import xyz.gnarbot.gnar.utils.Context;

import java.util.concurrent.TimeUnit;

@Command(
        aliases = "deletemsg",
        usage = "-msg_id...",
        description = "Delete those messages..",
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = Permission.MESSAGE_MANAGE
)
public class DeleteMessageCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("Please input message ID(s) to queue them for deletion.").queue();
            return;
        }

        if (args.length > 3) {
            context.send().error("Use `_prune` instead if you're going to delete more than 3 messages.").queue();
            return;
        }

        for (String id : args) {
            context.send().getChannel().getMessageById(id).queue(msg -> msg.delete().queue());
        }

        context.send().info("Deleted the context.getMessage().\nDeleting this message in **5** seconds.")
                .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}



