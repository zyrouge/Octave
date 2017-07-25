package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Scope;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 81,
        aliases = "autodel",
        scope = Scope.GUILD,
        permissions = Permission.MANAGE_SERVER,
        category = Category.SETTINGS
)
public class AutoDeleteCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (context.getGuildOptions().isAutoDelete()) {
            context.getGuildOptions().setAutoDelete(false);
            context.getGuildOptions().save();

            context.send().embed("Auto-Delete")
                    .setDescription("The bot will no longer automatically delete messages after 10 seconds.")
                    .action().queue();
        } else {
            context.getGuildOptions().setAutoDelete(true);
            context.getGuildOptions().save();

            context.send().embed("Auto-Delete")
                    .setDescription("The bot will delete messages after 10 seconds.")
                    .action().queue();
        }
    }
}
