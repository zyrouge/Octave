package xyz.gnarbot.gnar.commands.template.rewrite;

import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.commands.template.Executor;
import xyz.gnarbot.gnar.utils.Context;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommandCursor extends CommandExecutor implements Cursor {
    private final Map<String, Cursor> cursors;

    public CommandCursor() {
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
            Cursor current = this;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];

                Cursor next = current.getCursors().get(part);
                if (next == null) {
                    next = new StringCursor();
                    current.getCursors().put(part, next);
                }

                current = next;
            }

            current.add(parts[parts.length - 1], new MethodCursor(this, method.getAnnotation(Executor.class), method));
        }
    }

    @Override
    public Map<String, Cursor> getCursors() {
        return cursors;
    }

    @Override
    public void execute(Context context, String label, String[] args) {
        execute(context, args);
    }
}
