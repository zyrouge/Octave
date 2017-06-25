package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = "save",
        admin = true,
        category = Category.NONE
)
public class SaveCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {

//        context.send().info("Saved objects but release " + Bot.getGuildDataMap().size() + " GD objects.").queue();
    }
}
