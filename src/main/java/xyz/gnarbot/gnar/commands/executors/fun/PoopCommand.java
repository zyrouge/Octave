package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.util.StringJoiner;

@Command(
        aliases = {"poop"},
        usage = "[string]",
        description = "Shit your heart out.",
        category = Category.FUN)
public class PoopCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
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

        context.send().embed("Pooping Memes")
                .setColor(context.getConfig().getAccentColor())
                .setDescription(joiner.toString())
                .action().queue();
    }
}