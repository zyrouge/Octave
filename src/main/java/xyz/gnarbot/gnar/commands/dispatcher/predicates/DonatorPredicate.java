package xyz.gnarbot.gnar.commands.dispatcher.predicates;

import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.awt.*;
import java.util.function.BiPredicate;

public class DonatorPredicate implements BiPredicate<CommandExecutor, Context> {
    private static final String donatorMessage =
            "🌟 This command is for donators' servers only.\n" +
            "In order to enjoy donator perks, please consider pledging to " +
            "__**[our Patreon](https://www.patreon.com/gnarbot)**__.\n" +
            "Once you donate, join our __**[support guild](http://discord.gg/NQRpmr2)**__ " +
            "and ask one of the owners.";

    @Override
    public boolean test(CommandExecutor cmd, Context context) {
        if (cmd.getInfo().donor() && !context.getData().isPremium()) {
            context.send().embed("Donators Only")
                    .setColor(Color.ORANGE)
                    .setDescription(donatorMessage)
                    .action().queue();
            return false;
        }
        return true;
    }
}
