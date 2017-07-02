package xyz.gnarbot.gnar.commands.managed;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.ResponseBuilder;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class ManagedCommand extends CommandExecutor {
    private final List<Entry> paths = new ArrayList<>();

    public ManagedCommand() {
        Method[] methods = getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparingInt(a -> {
            Executor an = a.getAnnotation(Executor.class);
            if (an == null) {
                return 0;
            }
            return an.value();
        }));
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Executor.class)) continue;
            if (method.getParameters()[0].getType() != Context.class) {
                throw new RuntimeException("?");
            }

            Parser[] parsers = new Parser[method.getParameterCount()];
            parsers[0] = Parser.of(method.getName());
            Parameter[] params = method.getParameters();
            for (int i = 1; i < parsers.length; i++) {
                if (params[i].getType() == String.class) {
                    parsers[i] = Parser.STRING;
                } else if (params[i].getType() == int.class
                        || params[i].getType() == Integer.class) {
                    parsers[i] = Parser.INTEGER;
                } else if (params[i].getType() == Member.class) {
                    parsers[i] = Parser.MEMBER;
                } else if (params[i].getType() == Channel.class) {
                    parsers[i] = Parser.CHANNEL;
                } else if (params[i].getType() == Role.class) {
                    parsers[i] = Parser.ROLE;
                } else if (params[i].getType() == Duration.class) {
                    parsers[i] = Parser.DURATION;
                }
            }

            addPath(new Entry(parsers, method.getAnnotation(Executor.class).description(), method));
        }
    }

    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            noMatches(context, args);
            return;
        }

        main: for (Entry entry : paths) {
            Parser[] types = entry.parsers;
            if (args.length < types.length) continue;

            Object[] arguments = new Object[types.length];
            arguments[0] = context;

            if (types[0].parse(context, args[0]) == null) {
                continue;
            }

            for (int i = 1; i < types.length; i++) {
                Parser type = types[i];

                arguments[i] = i == types.length - 1
                        ? type.parse(context, StringUtils.join(Arrays.copyOfRange(args, i, args.length), " "))
                        : type.parse(context, args[i]);

                if (arguments[i] == null) {
                    continue main;
                }
            }
            try {
                entry.method.invoke(this, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }

        noMatches(context, args);
    }

    protected void noMatches(Context context, String[] args) {
        noMatches(context, args, null);
    }

    protected void noMatches(Context context, String[] args, String info) {
        StringBuilder builder = new StringBuilder();

        for (Entry entry : paths) {
            builder.append('`');
            for (int i = 0; i < entry.parsers.length; i++) {
                builder.append(entry.parsers[i].getName());
                if (i < entry.parsers.length - 1) {
                    builder.append(' ');
                }
            }
            builder.append("`");
            if (entry.description != null) {
                builder.append(" • ").append(entry.description);
            }
            builder.append("\n");
        }

        ResponseBuilder.ResponseEmbedBuilder eb = context.send().embed("_" + getInfo().aliases()[0]);

        if (args.length != 0) {
            eb.setTitle("Invalid arguments.");
            eb.setColor(Color.RED);
        }
        eb.appendDescription(getInfo().description());
        if (info != null) eb.appendDescription("\n" + info);
        eb.addField("Arguments", builder.toString(), false);
        eb.action().queue();
    }

    private void addPath(Entry entry) {
        paths.add(entry);
    }

    private class Entry {
        private final Parser[] parsers;
        private final String description;
        private final Method method;

        private Entry(Parser[] parsers, String description, Method method) {
            this.parsers = parsers;
            this.description = description;
            this.method = method;
        }
    }
}
