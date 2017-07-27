package xyz.gnarbot.gnar.commands.template;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.EmbedMaker;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

public class MethodTemplate implements Template {
    private final CommandTemplate command;
    private final Executor annotation;
    private final Method method;
    private final Parser[] parsers;

    public MethodTemplate(CommandTemplate command, Executor annotation, Method method) {
        this.command = command;
        this.annotation = annotation;
        this.method = method;

        Parameter[] params = method.getParameters();
        this.parsers = new Parser[params.length - 1];

        for (int i = 0; i < parsers.length; i++) {
            Class<?> type = params[i + 1].getType();
            Parser<?> parser = Parser.ofClass(type);

            if (parser == null) {
                throw new IllegalArgumentException("No parsers available for type " + type);
            }

            parsers[i] = parser;
        }
    }

    @Override
    public String description() {
        return annotation.description();
    }

    public String requirements() {
        StringBuilder builder = new StringBuilder();
        for (Parser parser : parsers) {
            builder.append(' ').append(parser.getName());
        }
        return builder.toString();
    }

    @Override
    public void execute(Context context, String[] args) {
        if (args.length < method.getParameterCount() - 1) {
            EmbedMaker eb = new EmbedMaker();

            eb.setTitle("Insufficient Arguments");
            eb.setDescription("Insufficient arguments, required: `" + requirements() + "`.");

            StringBuilder builder = new StringBuilder(parsers.length * 16);
            for (Parser parser : parsers) {
                builder.append("\n`").append(parser.getName()).append("` • ").append(parser.getDescription()).append('.');
            }

            eb.field("Required Arguments", false, builder.toString());
            eb.setColor(Color.RED);

            context.getChannel().sendMessage(eb.build()).queue();
            return;
        }

        Object[] arguments = new Object[method.getParameterCount()];
        arguments[0] = context;

        for (int i = 0; i < method.getParameterCount() - 1; i++) {
            String str;

            if (i == method.getParameterCount() - 2) {
                str = StringUtils.join(args, ' ');
            } else {
                str = args[i];
            }

            Object obj = parsers[i].parse(context, str);

            if (obj == null) {
                context.send().error("`" + str + "` did not match `" + parsers[i].getName() + "`.").queue();
            }

            arguments[i + 1] = obj;
        }

        try {
            method.invoke(command, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(String key, Template template) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Template> getCursors() {
        return Collections.emptyMap();
    }
}
