package xyz.gnarbot.gnar.commands.template;

import io.sentry.Sentry;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.commands.template.annotations.Description;
import xyz.gnarbot.gnar.commands.template.annotations.Name;
import xyz.gnarbot.gnar.commands.template.parser.Parser;
import xyz.gnarbot.gnar.commands.template.parser.Parsers;
import xyz.gnarbot.gnar.utils.EmbedMaker;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class MethodTemplate implements Template {
    private final CommandTemplate command;
    private final Description annotation;
    private final Method method;
    private final Parser[] parsers;

    @SuppressWarnings("unchecked")
    public MethodTemplate(CommandTemplate command, Description annotation, Method method, Map<Class<?>, Parser<?>> parsers) {
        this.command = command;
        this.annotation = annotation;
        this.method = method;

        Parameter[] params = method.getParameters();
        this.parsers = new Parser[params.length - 1];

        for (int i = 0; i < this.parsers.length; i++) {
            Parameter param = params[i + 1];
            Class<?> type = param.getType();

            Parser<?> parser = parsers.get(type);
            if (parser == null) {
                if (type.isEnum()) {
                    parser = Parsers.createEnumParser((Class) type);
                } else {
                    throw new IllegalArgumentException("No parsers available for type " + type);
                }
            }

            Name name = param.getAnnotation(Name.class);
            Description desc = param.getAnnotation(Description.class);

            if (name != null || desc != null) {
                parser = new Parser<>(
                        name != null ? name.value() : parser.getName(),
                        desc != null ? desc.value() : parser.getDescription(),
                        parser
                );
            }

            this.parsers[i] = parser;
        }
    }

    @Override
    public String description() {
        return annotation.value();
    }

    public String requirements() {
        StringBuilder builder = new StringBuilder();
        for (Parser parser : parsers) {
            builder.append(" (").append(parser.getName()).append(")");
        }
        return builder.toString();
    }

    @Override
    public void walk(Context context, String[] args, int depth) {
        if (args.length < method.getParameterCount() - 1) {
            EmbedMaker eb = new EmbedMaker();

            eb.setTitle("Insufficient Arguments");
            eb.description("Insufficient number of arguments, required:\n`" + requirements() + "`.");

            StringBuilder builder = new StringBuilder(parsers.length * 16);
            for (Parser parser : parsers) {
                builder.append("\n`(").append(parser.getName()).append(")` • ").append(parser.getDescription()).append('.');
            }

            eb.field("Required Arguments", false, builder.toString());
            eb.setColor(Color.RED);

            context.getTextChannel().sendMessage(eb.build()).queue();
            return;
        }

        Object[] arguments = new Object[method.getParameterCount()];
        arguments[0] = context;

        for (int i = 0; i < method.getParameterCount() - 1; i++) {
            String str;

            if (i == method.getParameterCount() - 2) {
                str = StringUtils.join(Arrays.copyOfRange(args, i, args.length), ' ');
            } else {
                str = args[i];
            }

            Object obj = parsers[i].parse(context, str);

            if (obj == null) {
                context.send().error("`" + str + "` does not match `(" + parsers[i].getName() + ")`.").queue();
                return;
            }

            arguments[i + 1] = obj;
        }

        try {
            method.invoke(command, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Sentry.capture(e);
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
