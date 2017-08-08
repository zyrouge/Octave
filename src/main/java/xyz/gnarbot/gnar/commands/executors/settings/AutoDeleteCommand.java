package xyz.gnarbot.gnar.commands.executors.settings;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Scope;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 81,
        aliases = {"autodelete", "autodel"},
        scope = Scope.GUILD,
        permissions = Permission.MANAGE_SERVER,
        category = Category.SETTINGS
)
public class AutoDeleteCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (context.getData().getCommand().isAutoDelete()) {
            context.getData().getCommand().setAutoDelete(false);
            context.getData().save();

            context.send().embed("Auto-Delete")
                    .setDescription("The bot will no longer automatically delete messages after 10 seconds.")
                    .action().queue();
        } else {
            context.getData().getCommand().setAutoDelete(true);
            context.getData().save();

            context.send().embed("Auto-Delete")
                    .setDescription("The bot will delete messages after 10 seconds.")
                    .action().queue();
        }
    }
}
