package xyz.gnarbot.gnar.commands.template.rewrite;

import xyz.gnarbot.gnar.commands.template.Executor;
import xyz.gnarbot.gnar.commands.template.Parser;
import xyz.gnarbot.gnar.utils.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

public class MethodCursor implements Cursor {
    private final CommandCursor command;
    private final Executor annotation;
    private final Method method;
    private final Parser[] parsers;

    public MethodCursor(CommandCursor command, Executor annotation, Method method) {
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
            context.send().error("Insufficient arguments, required: `" + requirements() + "`.").queue();
            return;
        }

        Object[] arguments = new Object[method.getParameterCount()];
        arguments[0] = context;

        for (int i = 0; i < method.getParameterCount() - 1; i++) {
            Object obj = parsers[i].parse(context, args[i]);
            if (obj == null) {
                context.send().error("Didn't match").queue();
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
    public void add(String key, Cursor cursor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Cursor> getCursors() {
        return Collections.emptyMap();
    }
}
