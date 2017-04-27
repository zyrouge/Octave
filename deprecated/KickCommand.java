package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.Scope;
import xyz.gnarbot.gnar.utils.Context;

@Command(aliases = "kick",
        usage = "-user",
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = Permission.KICK_MEMBERS)
public class KickCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        Member author = context.getMessage().getMember();
        Member target = null;

        if (context.getMessage().getMentionedChannels().size() >= 1) {
            target = getGuild().getMember(context.getMessage().getMentionedUsers().get(0));
        } else if (args.length >= 1) {
            target = getGuildData().getMemberByName(args[0], false);
        }

        if (target == null) {
            context.send().error("Could not find user.").queue();
            return;
        }

        if (!author.canInteract(target)) {
            context.send().error("Sorry, that user has an equal or higher role.").queue();
            return;
        }

        getGuild().getController().kick(target).queue();
        context.send().info(target.getEffectiveName() + " has been kicked.").queue();
    }
}
