package xyz.gnarbot.gnar.commands.executors.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.util.HashMap;
import java.util.Map;

@Command(aliases = {"leet"}, usage = "(string)", description = "Leet a string!", category = Category.FUN)
public class LeetifyCommand extends CommandExecutor {
    private static final Map<String, String> substitutions = new HashMap<String, String>() {{
        put("a", "4");
        put("A", "@");
        put("G", "6");
        put("e", "3");
        put("l", "1");
        put("s", "5");
        put("S", "\\$");
        put("o", "0");
        put("t", "7");
        put("i", "!");
        put("I", "1");
        put("B", "|3");
    }};

    @Override
    public void execute(Context context, String[] args) {
        String s = StringUtils.join(args, " ");

        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            s = s.replaceAll(entry.getKey(), entry.getValue());
        }

        context.send().embed("Leet it")
                .setColor(context.getBot().getConfig().getAccentColor())
                .setDescription(s)
                .action().queue();
    }
}
