package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

@Command(
        id = 87,
        aliases = {"nyan"},
        description = "NYAN NYAN NYAN NYAN NYAN.",
        category = Category.FUN
)
public class NyanCommand extends CommandExecutor{

    @Override
    public void execute(Context context, String[] args) {
        context.send().embed()
                .setImage(Utils.getRamMoeImage("nyan"))
                .action().queue();
    }

}
