package xyz.gnarbot.gnar.commands.managed;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.ResponseBuilder;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class ManagedCommand extends CommandExecutor {
    protected final List<Entry> paths = new ArrayList<>();

    public ManagedCommand() {
        Method[] methods = getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparingInt(a -> {
            Executor an = a.getAnnotation(Executor.class);
            if (an == null) {
                return 0;
            }
            return an.position();
        }));
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Executor.class)) continue;
            if (method.getParameters()[0].getType() != Context.class) {
                throw new RuntimeException("?");
            }
            Arg[] args = new Arg[method.getParameterCount()];
            args[0] = Arg.of(method.getName());
            Parameter[] params = method.getParameters();
            for (int i = 1; i < args.length; i++) {
                if (params[i].getType() == String.class) {
                    args[i] = Arg.STRING;
                } else if (params[i].getType() == int.class
                        || params[i].getType() == Integer.class) {
                    args[i] = Arg.INTEGER;
                } else if (params[i].getType() == Member.class) {
                    args[i] = Arg.MEMBER;
                } else if (params[i].getType() == Channel.class) {
                    args[i] = Arg.CHANNEL;
                } else if (params[i].getType() == Role.class) {
                    args[i] = Arg.ROLE;
                }
            }

            addPath(new Entry(args, method.getAnnotation(Executor.class).description(), method));
        }
    }

    @Override
    public void execute(Context context, String[] args) {
        if (args.length == 0) {
            noMatches(context, args);
            return;
        }

        main: for (Entry entry : paths) {
            Arg[] types = entry.args;
            if (args.length != types.length) continue;

            Object[] arguments = new Object[args.length];
            arguments[0] = context;

            if (types[0].parse(context, args[0]) == null) {
                continue;
            }

            for (int i = 1; i < types.length; i++) {
                Arg type = types[i];

                arguments[i] = type.parse(context, args[i]);

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
            for (int i = 0; i < entry.args.length; i++) {
                builder.append(entry.args[i].getName());
                if (i < entry.args.length - 1) {
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

    protected class Entry {
        private final Arg[] args;
        private final String description;
        private final Method method;

        private Entry(Arg[] args, String description, Method method) {
            this.args = args;
            this.description = description;
            this.method = method;
        }

        public Arg[] getArgs() {
            return args;
        }

        public String getDescription() {
            return description;
        }

        public Method getExecutor() {
            return method;
        }
    }
}
