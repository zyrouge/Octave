package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

@Command(
        id = 10,
        aliases = "ttb",
        usage = "(words...)",
        description = "Text to bricks fun.",
        category = Category.FUN
)
public class TextToBrickCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("Please provide words. `_ttb meme`").queue();
            return;
        }

        char[] array = StringUtils.join(args, " ").toUpperCase().toCharArray();

        if (array.length > 100) {
            context.send().error("Alright, that's way too long.").queue();
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (char c : array) {
            if (Character.isLetter(c)) {
                sb.appendCodePoint(0x1F1E6 + c - 'A');
            } else {
                sb.append(c);
            }
            sb.append(' ');
        }

        if (sb.length() > 2000) {
            context.send().error("Too many characters.").queue();
            return;
        }

        context.send().text(sb.toString()).queue();
    }
}
