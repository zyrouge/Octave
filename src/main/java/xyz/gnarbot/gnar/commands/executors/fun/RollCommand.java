package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.ResponseBuilder;

import java.util.StringJoiner;

@Command(
        id = 9,
        aliases = {"roll"},
        usage = "[(rolls)d](max value)",
        description = "Roll a random number from 0 to argument.",
        category = Category.FUN
)
public class RollCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length >= 1) {
            int rolls = 1;
            int maxValue;

            try {
                String[] parts = args[0].split("d", 2);

                if (parts.length == 1) {
                    maxValue = Integer.valueOf(parts[0]);
                } else {
                    rolls = Integer.valueOf(parts[0]);
                    maxValue = Integer.valueOf(parts[1]);
                }
            } catch (NumberFormatException e) {
                context.send().error("That wasn't a number or a valid dice configuration.").queue();
                return;
            }

            if (maxValue <= 0) {
                context.send().error("Max value need to be greater than 0.").queue();
                return;
            } else if (maxValue > 10000) {
                maxValue = 10000;
            }

            if (rolls > 20) {
                rolls = 20;
            }

            ResponseBuilder.ResponseEmbedBuilder eb = context.send().embed()
                    .setTitle(rolls + " \uD83C\uDFB2 [1 — " + maxValue + "]");

            if (rolls == 1) {
                eb.setDescription(
                        "You rolled a **" + (int) (Math.random() * maxValue + 1) +
                                "**.\nTry an advanced configuration like `2d6`!"
                ).action().queue();
            } else {
                StringJoiner joiner = new StringJoiner(", ");
                for (int i = 0; i < rolls; i++) {
                    joiner.add(String.valueOf((int) (Math.random() * maxValue + 1)));
                }

                eb.setDescription("You rolled **" + joiner + "**.");

                eb.action().queue();
            }


        } else {
            context.send().error("Insufficient amount of arguments. ie. `_roll 5` or `_roll 2d6`").queue();
        }
    }
}
