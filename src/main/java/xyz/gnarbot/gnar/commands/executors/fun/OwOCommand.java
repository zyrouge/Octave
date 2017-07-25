package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        id = 88,
        aliases = {"owo"},
        description = "Wuts this.",
        category = Category.FUN
)
public class OwOCommand extends CommandExecutor{

    @Override
    public void execute(Context context, String[] args) {
        context.send().embed()
                .setDescription("OwO wuts this")
                .setImage(Utils.getRamMoeImage("owo"))
                .action().queue();
    }

}
