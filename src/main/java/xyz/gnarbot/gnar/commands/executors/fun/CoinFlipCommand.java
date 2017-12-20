package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = {"coinflip", "flip"},
        description = "Heads or Tails?"
)
@BotInfo(
        id = 3,
        category = Category.FUN
)
public class CoinFlipCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        context.send().embed().setDesc((int) (Math.random() * 2) == 0 ? "Heads!" : "Tails!").action().queue();
    }
}