package xyz.gnarbot.gnar.commands.executors.fun;

import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

import java.util.Random;

@Command(aliases = {"coinflip", "flip"}, description = "Heads or Tails?", category = Category.FUN)
public class CoinFlipCommand extends CommandExecutor {
    @Override
    public void execute(Message message, String[] args) {
        message.send().embed("Coin Flip")
                .setColor(BotConfiguration.ACCENT_COLOR)
                .setDescription(new Random().nextInt(2) == 0 ? "Heads" : "Tails!")
                .rest().queue();
    }
}