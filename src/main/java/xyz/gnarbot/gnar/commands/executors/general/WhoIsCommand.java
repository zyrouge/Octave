package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Command(aliases = {"whois", "infoof", "infoon", "user"},
        usage = "-@user",
        description = "Get information on a user."
)
public class WhoIsCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            context.send().error("You did not mention a user.").queue();
            return;
        }

        // SEARCH USERS
        final Member member;

        List<User> mentioned = context.getMessage().getMentionedUsers();
        if (mentioned.size() > 0) {
            member = context.getGuild().getMember(mentioned.get(0));
        } else {
            member = context.getGuildData().getMemberByName(StringUtils.join(args, " "), true);
        }

        if (member == null) {
            context.send().error("You did not mention a valid user.").queue();
            return;
        }

        context.send().embed("Who is " + member.getUser().getName() + "?")
                .setColor(member.getColor())
                .setThumbnail(member.getUser().getAvatarUrl())
                .field("Name", true, member.getUser().getName())
                .field("Discriminator", true, member.getUser().getDiscriminator())

                .field("ID", true, member.getUser().getId())
                .field("Join Date", true, member.getJoinDate().format(DateTimeFormatter.ISO_LOCAL_DATE))

                .field("Nickname", true, member.getNickname() != null ? member.getNickname() : "No nickname.")
                .field("Game", true, member.getGame() != null ? member.getGame().getName() : "No game.")

                .field("Roles", false, () -> {
                    StringBuilder sb = new StringBuilder();
                    for (Role role : member.getRoles()) {
                        sb.append("• ").append(role.getName()).append('\n');
                    }
                    return sb.toString();
                })
                .action().queue();
    }
}
