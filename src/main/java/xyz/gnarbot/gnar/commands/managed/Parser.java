package xyz.gnarbot.gnar.commands.managed;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.utils.Context;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static final Parser STRING = new Parser("(string)") {
        private final Pattern pattern = Pattern.compile("[a-zA-z][\\w\\d]*");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? s : null;
        }
    };
    public static final Parser INTEGER = new Parser("(integer)") {
        private final Pattern pattern = Pattern.compile("\\d+");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? Integer.valueOf(s) : null;
        }
    };
    public static final Parser MEMBER = new Parser("(@user)") {
        private final Pattern pattern = Pattern.compile("<@!?(\\d+)>");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getMemberById(matcher.group(1));
            } else {
                List<Member> list = c.getGuild().getMembersByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser CHANNEL = new Parser("(#channel)") {
        private final Pattern pattern = Pattern.compile("<#(\\d+)>");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getTextChannelById(matcher.group(1));
            } else {
                List<TextChannel> list = c.getGuild().getTextChannelsByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser ROLE = new Parser("(@role)") {
        private final Pattern pattern = Pattern.compile("<@&(\\d+)>");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return c.getGuild().getRoleById(matcher.group(1));
            } else {
                List<Role> list = c.getGuild().getRolesByName(s, false);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    };
    public static final Parser DURATION = new Parser("(time)") {
        private final Pattern pattern = Pattern.compile("^(\\d+)(?::(\\d+))?(?::(\\d+))?$");

        @Override
        public Object parse(Context c, String s) {
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

    public static Parser of(String string) {
        return new Parser(string) {
            @Override
            public Object parse(Context c, String s) {
                return s.equals(string) ? s : null;
            }
        };
    }

    public String getName() {
        return name;
    }

    public Object parse(Context c, String s) {
        return s;
    }
}