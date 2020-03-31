package xyz.gnarbot.gnar.commands.fun;

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
        context.send().info((int) (Math.random() * 2) == 0 ? "Heads!" : "Tails!").queue();
    }
}