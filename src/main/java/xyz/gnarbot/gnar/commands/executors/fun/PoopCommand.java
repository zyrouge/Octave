package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.*;

import java.util.StringJoiner;

@Command(
        aliases = {"poop"},
        usage = "[string]",
        description = "Shit your heart out."
)
@BotInfo(
        id = 8,
        category = Category.FUN
)
public class PoopCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String poop = StringUtils.join(args, " ");

        StringJoiner joiner = new StringJoiner("\n", "```\n", "```");

        joiner.add("░░░░░░░░░░░█▀▀░░█░░░░░░");
        joiner.add("░░░░░░▄▀▀▀▀░░░░░█▄▄░░░░");
        joiner.add("░░░░░░█░█░░░░░░░░░░▐░░░");
        joiner.add("░░░░░░▐▐░░░░░░░░░▄░▐░░░");
        joiner.add("░░░░░░█░░░░░░░░▄▀▀░▐░░░");
        joiner.add("░░░░▄▀░░░░░░░░▐░▄▄▀░░░░");
        joiner.add("░░▄▀░░░▐░░░░░█▄▀░▐░░░░░");
        joiner.add("░░█░░░▐░░░░░░░░▄░▌░░░░░");
        joiner.add("░░░█▄░░▀▄░░░░▄▀█░▌░░░░░");
        joiner.add("░░░▌▐▀▀▀░▀▀▀▀░░█░▌░░░░░");
        joiner.add("░░▐▌▐▄░░▀▄░░░░░█░█▄▄░░░");

        StringBuilder poopArt = new StringBuilder("░░░▀▀░▄███▄▄░░░▀▄▄▄▀░░░");

        for (int i = 0; i < poop.length(); i++) {
            try {
                poopArt.setCharAt(7 + i, poop.charAt(i));
            } catch (IndexOutOfBoundsException e) {
                context.send().error("Poop is too big. Constipation occurred.").queue();
                return;
            }
        }

        joiner.add(poopArt);
        joiner.add("░░░░░░░░░░░░░░░░░░░░░░░");

        context.send().info(joiner.toString()).queue();
    }
}