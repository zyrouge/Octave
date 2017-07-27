package xyz.gnarbot.gnar.commands.template;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser<T> {
    public static final Parser<String> STRING = new Parser<String>("(string)") {
        @Override
        public String parse(Context c, String s) {
            return s;
        }
    };
    public static final Parser<Integer> INTEGER = new Parser<Integer>("(integer)") {
        private final Pattern pattern = Pattern.compile("\\d+");

        @Override
        public Integer parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? Integer.valueOf(s) : null;
        }
    };
    public static final Parser<Member> MEMBER = new Parser<Member>("(@user)") {
        private final Pattern pattern = Pattern.compile("<@!?(\\d+)>");

        @Override
        public Member parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getMemberById(matcher.group(1));
            } else {
                List<Member> list = c.getGuild().getMembersByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser<TextChannel> TEXT_CHANNEL = new Parser<TextChannel>("(#channel)") {
        private final Pattern pattern = Pattern.compile("<#(\\d+)>");

        @Override
        public TextChannel parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getTextChannelById(matcher.group(1));
            } else {
                List<TextChannel> list = c.getGuild().getTextChannelsByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser<VoiceChannel> VOICE_CHANNEL = new Parser<VoiceChannel>("(voice channel)") {
        @Override
        public VoiceChannel parse(Context c, String s) {
            List<VoiceChannel> list = c.getGuild().getVoiceChannelsByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    };
    public static final Parser<Role> ROLE = new Parser<Role>("(@role)") {
        private final Pattern pattern = Pattern.compile("<@&(\\d+)>");

        @Override
        public Role parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getRoleById(matcher.group(1));
            } else {
                List<Role> list = c.getGuild().getRolesByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser<CommandExecutor> COMMAND = new Parser<CommandExecutor>("(_command)") {
        @Override
        public CommandExecutor parse(Context c, String s) {
            if (s.startsWith("_")) {
                return Bot.getCommandRegistry().getCommand(s.substring(1));
            } else {
                return Bot.getCommandRegistry().getCommand(s);
            }
        }
    };
    public static final Parser<Category> CATEGORY = new Parser<Category>("([category])") {
        private final Pattern pattern = Pattern.compile("\\[(\\w+)]");

        @Override
        public Category parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                try {
                    return Category.valueOf(matcher.group(1).toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    };
    public static final Parser<Duration> DURATION = new Parser<Duration>("(time hh:mm:ss)") {
        private final Pattern pattern = Pattern.compile("^(\\d+)(?::(\\d+))?(?::(\\d+))?$");

        @Override
        public Duration parse(Context c, String s) {
            Matcher m = pattern.matcher(s);

            long seconds = 0;
            long minutes = 0;
            long hours = 0;

            if (!m.find()) {
                return null;
            }

            int capturedGroups = 0;
            if (m.group(1) != null) capturedGroups++;
            if (m.group(2) != null) capturedGroups++;
            if (m.group(3) != null) capturedGroups++;

            switch (capturedGroups) {
                case 0:
                    return null;
                case 1:
                    seconds = Long.parseLong(m.group(1));
                    break;
                case 2:
                    minutes = Long.parseLong(m.group(1));
                    seconds = Long.parseLong(m.group(2));
                    break;
                case 3:
                    hours = Long.parseLong(m.group(1));
                    minutes = Long.parseLong(m.group(2));
                    seconds = Long.parseLong(m.group(3));
                    break;
            }

            minutes = minutes + hours * 60;
            seconds = seconds + minutes * 60;

            return Duration.ofSeconds(seconds);
        }
    };

    private final String name;

    private Parser(String name) {
        this.name = name;
    }

    public static Parser<String> of(String keyword) {
        return new Parser<String>(keyword) {
            @Override
            public String parse(Context c, String s) {
                return s.equals(keyword) ? s : null;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Parser && keyword.equals(((Parser) obj).name);
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(new String[] {"Parser_s=", keyword});
            }
        };
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Nullable
    public abstract T parse(Context c, String s);

    private static final HashMap<Class<?>, Parser<?>> parserMap = new HashMap<>();
    static {
        parserMap.put(String.class, STRING);
        parserMap.put(int.class, INTEGER);
        parserMap.put(Integer.class, INTEGER);
        parserMap.put(Member.class, MEMBER);
        parserMap.put(Role.class, ROLE);
        parserMap.put(TextChannel.class, TEXT_CHANNEL);
        parserMap.put(VoiceChannel.class, VOICE_CHANNEL);
        parserMap.put(Duration.class, DURATION);
        parserMap.put(CommandExecutor.class, COMMAND);
        parserMap.put(Category.class, CATEGORY);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> ofClass(Class<T> cls) {
        return (Parser<T>) parserMap.get(cls);
    }
}