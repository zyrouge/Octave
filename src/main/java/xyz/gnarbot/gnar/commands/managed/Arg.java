package xyz.gnarbot.gnar.commands.managed;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.utils.Context;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Arg {
    public static final Arg STRING = new Arg("string") {
        private final Pattern pattern = Pattern.compile("[a-zA-z][\\w\\d]*");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? s : null;
        }
    };
    public static final Arg INTEGER = new Arg("integer") {
        private final Pattern pattern = Pattern.compile("\\d+");

        @Override
        public Object parse(Context c, String s) {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? Integer.valueOf(s) : null;
        }
    };
    public static final Arg MEMBER = new Arg("@user") {
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
    public static final Arg CHANNEL = new Arg("#channel") {
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
    public static final Arg ROLE = new Arg("@role") {
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

    private final String name;

    private Arg(String name) {
        this.name = name;
    }

    public static Arg of(String string) {
        return new Arg(string) {
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