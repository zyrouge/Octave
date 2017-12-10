package xyz.gnarbot.gnar.commands.dispatcher.predicates;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Context;

import java.util.function.BiPredicate;

public class AdministratorPredicate implements BiPredicate<CommandExecutor, Context> {
    @Override
    public boolean test(CommandExecutor cmd, Context context) {
        if (cmd.getBotInfo().admin() && !Bot.CONFIG.getAdmins().contains(context.getMember().getUser().getIdLong())) {
            context.send().error("This command is for bot administrators only.").queue();
            return false;
        }
        return true;
    }
}
