package xyz.gnarbot.gnar.commands.executors.media;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

import java.util.Arrays;

@Command(
        id = 78,
        aliases = {
                "action", "cry", "cuddle", "hug", "kiss", "lewd", "lick",
                "nom", "nyan", "owo", "pat", "potato", "pout", "rem",
                "smug", "stare", "tickle", "triggered", "slap"
        },
        description = "Unleash your unrelenting yet suppressed emotions.",
        usage = "(action)",
        category = Category.MEDIA
)
public class ActionCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        String action;

        if (!label.equals("action")) {
            action = label;
        } else if (args.length > 0) {
            action = args[0];
        } else {
            context.send().error("Try one of these actions: `" + Arrays.toString(getInfo().aliases()) + "`").queue();
            return;
        }

        if (!Arrays.asList(getInfo().aliases()).contains(action)) {
            context.send().error("This isn't one of the available actions.").queue();
            return;
        }

        context.send().embed().setImage(Utils.getRamMoeImage(action)).action().queue();
    }
}
