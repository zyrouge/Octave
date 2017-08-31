package xyz.gnarbot.gnar.commands.template;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser<T> {
    public static final Parser<String> STRING = new Parser<String>("string", "Plain text") {
        @Override
        public String parse(Context c, String s) {
            return s;
        }
    };
    public static final Parser<Integer> INTEGER = new Parser<Integer>("integer", "Integer number") {
        @Override
        public Integer parse(Context c, String s) {
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    };
    public static final Parser<Double> DECIMAL = new Parser<Double>("number", "Number, can be decimal") {
        @Override
        public Double parse(Context c, String s) {
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    };
    public static final Parser<Duration> DURATION = new Parser<Duration>("hh:mm:ss", "Timestamp") {
        private final Pattern pattern = Pattern.compile("^(?:(?:(\\d+):)?(\\d+):)?(\\d+)$");

        @Override
        public Duration parse(Context c, String s) {
            Matcher m = pattern.matcher(s);

            if (!m.find()) {
                return null;
            }

            String[] group = { m.group(1), m.group(2), m.group(3) };
            long hours = group[0] == null ? 0 : Long.parseLong(group[0]);
            long minutes = group[1] == null ? 0 : Long.parseLong(group[1]);
            long seconds = group[2] == null ? 0 : Long.parseLong(group[2]);

            minutes += hours * 60;
            seconds += minutes * 60;

            return Duration.ofSeconds(seconds);
        }
    };

    public static final Parser<Member> MEMBER = new Parser<Member>("@user", "User mention or name") {
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
    public static final Parser<TextChannel> TEXT_CHANNEL = new Parser<TextChannel>("#channel", "Channel mention or name") {
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
    public static final Parser<VoiceChannel> VOICE_CHANNEL = new Parser<VoiceChannel>("voice channel", "Voice channel name") {
        @Override
        public VoiceChannel parse(Context c, String s) {
            List<VoiceChannel> list = c.getGuild().getVoiceChannelsByName(s, false);
            return list.isEmpty() ? null : list.get(0);
        }
    };
    public static final Parser<Role> ROLE = new Parser<Role>("@role", "Role mention or name") {
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
    public static final Parser<CommandExecutor> COMMAND = new Parser<CommandExecutor>("_command", "Command label") {
        @Override
        public CommandExecutor parse(Context c, String s) {
            if (s.startsWith("_")) {
                return Bot.getCommandRegistry().getCommand(s.substring(1));
            } else {
                return Bot.getCommandRegistry().getCommand(s);
            }
        }
    };

    private final String name;
    private final String description;

    private Parser(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        parserMap.put(double.class, DECIMAL);
        parserMap.put(Double.class, DECIMAL);
        parserMap.put(Member.class, MEMBER);
        parserMap.put(Role.class, ROLE);
        parserMap.put(TextChannel.class, TEXT_CHANNEL);
        parserMap.put(VoiceChannel.class, VOICE_CHANNEL);
        parserMap.put(Duration.class, DURATION);
        parserMap.put(CommandExecutor.class, COMMAND);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> ofClass(Class<T> cls) {
        if (cls.isArray()) {
            return ofArray((Class) cls);
        } else if (cls.isEnum()) {
            return ofEnum((Class) cls);
        }

        return (Parser<T>) parserMap.get(cls);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T[]> ofArray(Class<T[]> cls) {
        Parser subparser = ofClass(cls.getComponentType());

        if (subparser == null) return null;

        return new Parser<T[]>(subparser.getName() + "...", subparser.getDescription()) {
            @Override
            public T[] parse(Context c, String s) {
                String[] strings = s.split(",\\s*|\n");
                T[] args = (T[]) Array.newInstance(cls.getComponentType(), strings.length);

                for (int i = 0; i < strings.length; i++) {
                    T obj = (T) subparser.parse(c, strings[i]);
                    if (obj == null) return null;
                    args[i] = obj;
                }

                System.out.println(Arrays.toString(args));
                return args;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Parser<T> ofEnum(Class<T> cls) {
        StringJoiner sj = new StringJoiner(", ");
        for (T item: cls.getEnumConstants()) {
            sj.add(item.name());
        }

        return new Parser<T>(sj.toString(), sj.toString()) {
            @Override
            public T parse(Context c, String s) {
                try {
                    return Enum.valueOf(cls, s.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
        };
    }
}