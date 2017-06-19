package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "save",
        admin = true,
        category = Category.NONE,
        ignorable = false
)
public class SaveCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        boolean force = false;
        if (args.length > 0) {
            force = Boolean.parseBoolean(args[0]);
        }

        Bot.DATABASE.pushToDatabase(force);

        if (Bot.getGuildDataMap().size() == 0) {
            context.send().info("Saved and released objects from memory.").queue();
        } else {
            context.send().info("Unable to save and release " + Bot.getGuildDataMap().size() + " GD objects.").queue();
        }
    }
}
