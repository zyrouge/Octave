package xyz.gnarbot.gnar.commands.fun;

import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.response.ResponseBuilder;

import java.util.Arrays;
import java.util.StringJoiner;

@Command(
        aliases = {"roll"},
        usage = "[(rolls)d](max value) ie. 2d6",
        description = "Roll a random number from 0 to argument."
)
@BotInfo(
        id = 9,
        category = Category.FUN
)
public class RollCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length < 1) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

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
            context.send().issue("That wasn't a number or a valid dice configuration. `_roll 5` or `_roll 2d6`").queue();
            return;
        }

        if (maxValue <= 0) {
            context.send().issue("Max value need to be greater than 0.").queue();
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
            eb.description("You rolled a **" + (int) (Math.random() * maxValue + 1) + "**.");
        } else {
            int sum = 0;

            int[] results = new int[rolls];
            for (int i = 0; i < rolls; i++) {
                results[i] = (int) (Math.random() * maxValue + 1);
                sum += results[i];
            }

            Arrays.sort(results);

            StringJoiner joiner = new StringJoiner(", ");
            for (int i : results) {
                joiner.add(String.valueOf(i));
            }
            eb.description("You rolled **" + joiner + "** = **" + sum + "**.");
        }
        eb.action().queue();
    }
}
