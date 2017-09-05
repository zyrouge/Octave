package xyz.gnarbot.gnar.commands.template;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.lang.reflect.Method;
import java.util.*;

public abstract class CommandTemplate extends CommandExecutor implements Template {
    private final Map<String, Template> cursors;

    public CommandTemplate() {
        this(Collections.emptyMap());
    }

    public CommandTemplate(Map<Class, Parser> parserOverrides) {
        cursors = new LinkedHashMap<>();

        Method[] methods = getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method method : methods) {
            Description ann = method.getAnnotation(Description.class);
            if (ann == null) continue;

            if (method.getParameterCount() == 0 || method.getParameters()[0].getType() != Context.class) {
                throw new RuntimeException("First argument of " + method + " must be Context");
            }

            String name = ann.display();
            if (name.isEmpty()) {
                name = method.getName();
            }

            String[] parts = name.split("_");
            Template current = this;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];

                Template next = current.getCursors().get(part);
                if (next == null) {
                    next = new StringTemplate();
                    current.getCursors().put(part, next);
                }

                current = next;
            }

            current.add(parts[parts.length - 1], new MethodTemplate(this, method.getAnnotation(Description.class), method, parserOverrides));
        }
    }

    @Override
    public Map<String, Template> getCursors() {
        return cursors;
    }

    @Override
    public void execute(Context context, String label, String[] args) {
        execute(context, args);
    }

    @Override
    public void helpMessage(Context context, String[] args, String title, String description) {
        Template.super.helpMessage(context, args,
                title == null ? Bot.CONFIG.getPrefix() + getInfo().aliases()[0] + " Command" : title,
                getInfo().description() + (description == null ? "" : description));
    }
}
