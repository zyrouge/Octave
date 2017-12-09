package xyz.avarel.core.jda;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import xyz.avarel.core.trees.parsers.Parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDAParsers {
    private static final Pattern memberPattern = Pattern.compile("<@!?(\\d+)>");
    public static final Parser<CommandContext, Member> MEMBER = new Parser<>("@user", "User mention or name", (c, s) -> {
        Matcher matcher = memberPattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getMemberById(matcher.group(1));
        } else {
            List<Member> list = c.getGuild().getMembersByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });

    private static final Pattern textChannelPattern = Pattern.compile("<#(\\d+)>");
    public static final Parser<CommandContext, TextChannel> TEXT_CHANNEL = new Parser<>("#channel", "Channel mention or name", (c, s) -> {
        Matcher matcher = textChannelPattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getTextChannelById(matcher.group(1));
        } else {
            List<TextChannel> list = c.getGuild().getTextChannelsByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });
    public static final Parser<CommandContext, VoiceChannel> VOICE_CHANNEL = new Parser<>("voice channel", "Voice channel name", (c, s) -> {
        List<VoiceChannel> list = c.getGuild().getVoiceChannelsByName(s, false);
        return list.isEmpty() ? null : list.get(0);
    });

    private static final Pattern rolePattern = Pattern.compile("<@&(\\d+)>");
    public static final Parser<CommandContext, Role> ROLE = new Parser<>("@role", "Role mention or name", (c, s) -> {
        Matcher matcher = rolePattern.matcher(s);
        if (matcher.find()) {
            return c.getGuild().getRoleById(matcher.group(1));
        } else {
            List<Role> list = c.getGuild().getRolesByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    });
}
