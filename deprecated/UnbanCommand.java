package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Scope;
import xyz.gnarbot.gnar.utils.Context;

@Command(aliases = "unban",
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = Permission.BAN_MEMBERS)
public class UnbanCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        context.getMessage().getGuild().getController().getBans().queue(bans -> {
            Member target = null;

            for (User user : bans) {
                if (user.getId().equals(args[0])) {
                    target = getGuild().getMember(user);
                    break;
                }
            }

            if (args.length >= 1) {
                target = getGuildData().getMemberByName(args[0], true);
            }
            if (target == null) {
                context.send().error("Could not find user.").queue();
                return;
            }

            getGuild().getController().unban(target).queue();
            context.send().info(target.getEffectiveName() + " has been unbanned.").queue();
        });
    }
}

