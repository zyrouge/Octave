package xyz.gnarbot.gnar.commands.template;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommandTemplate extends CommandExecutor implements Template {
    private final Map<String, Template> cursors;

    public CommandTemplate() {
        cursors = new LinkedHashMap<>();

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

            String[] parts = method.getName().split("_");
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

            current.add(parts[parts.length - 1], new MethodTemplate(this, method.getAnnotation(Executor.class), method));
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
