package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 3,
        aliases = {"coinflip", "flip"},
        description = "Heads or Tails?",
        category = Category.FUN
)
public class CoinFlipCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        context.send().embed().setDescription((int) (Math.random() * 2) == 0 ? "Heads!" : "Tails!").action().queue();
    }
}