package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        id = 91,
        aliases = {"rem"},
        description = "Rem best waifu.",
        category = Category.FUN
)
public class RemCommand extends CommandExecutor{

    @Override
    public void execute(Context context, String[] args) {
        context.send().embed()
                .setImage(Utils.getRamMoeImage("rem"))
                .action().queue();
    }

}
