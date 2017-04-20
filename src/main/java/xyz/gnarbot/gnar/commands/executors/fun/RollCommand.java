package xyz.gnarbot.gnar.commands.executors.fun;

import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

import java.util.Random;

@Command(
        aliases = {"roll"},
        usage = "-max_value",
        description = "Roll a random number from 0 to argument.",
        category = Category.FUN)
public class RollCommand extends CommandExecutor {
    @Override
    public void execute(Message message, String[] args) {
        try {
            if (args.length >= 1) {
                if (!(Integer.valueOf(args[0]) > 0)) {
                    message.send().error("Number need to be > 0.").queue();
                    return;
                }

                message.send().embed("Roll a Number")
                        .setColor(BotConfiguration.ACCENT_COLOR)
                        .description(() -> "You rolled a **"
                                + new Random().nextInt(Integer.valueOf(args[0]))
                                + "** from range **[0 to " + args[0]+ "]**.")
                        .rest().queue();

            } else {
                message.send().error("Insufficient amount of arguments.").queue();
            }
        } catch (Exception e) {
            message.send().error("Only numbers are allowed.\n\n**Example:**\n\n[_roll 10]()").queue();
        }
    }
}
