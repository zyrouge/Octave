package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.Utils;

import java.util.StringJoiner;

@Command(
        id = 16,
        aliases = {"guild", "server"},
        description = "Get information this guild."
)
public class GuildInfoCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        Guild guild = context.getGuild();

        StringJoiner roleStr = new StringJoiner(", ");
        for (Role role : guild.getRoles()) {
            roleStr.add(role.getName());
        }

        StringBuilder emoteStr = new StringBuilder();
        for (Emote emote : guild.getEmotes()) {
            emoteStr.append(emote.getAsMention()).append(' ');
        }

        context.send().embed("Guild Information")
                .setThumbnail(context.getGuild().getIconUrl())
                .field("Name", true, guild.getName())
                .field("ID", true, guild.getId())
                .field("Creation Time", true, guild.getCreationTime())
                .field("Owner", true, guild.getOwner().getAsMention())
                .field("Text Channels", true, guild.getTextChannels().size())
                .field("Voice Channels", true, guild.getVoiceChannels().size())
                .field("Members", true, guild.getMembers().size())
                .field("Emotes", true, emoteStr)
                .field("Roles", true, roleStr)
                .field("Premium", true, context.getData().isPremium()
                        ? "Premium status expires in `" + Utils.getTime(context.getData().remainingPremium()) + "`."
                        : "This guild does not have the premium status.\nVisit our __**[Patreon](https://www.patreon.com/gnarbot)**__ to find out more."
                ).action().queue();
    }
}