package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        id = 84,
        aliases = {"lewd"},
        description = "Wew.",
        category = Category.FUN
)
public class LewdCommand extends CommandExecutor{

    @Override
    public void execute(Context context, String[] args) {
        context.send().embed()
                .setImage(Utils.getRamMoeImage("lewd"))
                .action().queue();
    }

}
