package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        aliases = {"roll"},
        usage = "(max value)",
        description = "Roll a random number from 0 to argument.",
        category = Category.FUN
)
public class RollCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length >= 1) {
            int maxValue;
            try {
                maxValue = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                context.send().error("That wasn't a number.").queue();
                return;
            }

            if (maxValue <= 0) {
                context.send().error("Number need to be > 0.").queue();
                return;
            }

            context.send().embed()
                    .setDescription("\uD83C\uDFB2 You rolled a **" + (int) (Math.random() * maxValue + 1)
                            + "**   `[1 — " + maxValue + "]`")
                    .action().queue();
        } else {
            context.send().error("Insufficient amount of arguments. ie. `_roll 5`").queue();
        }
    }
}
