package xyz.avarel.core.trees;

import xyz.avarel.core.CommandExecutor;
import xyz.avarel.core.DispatcherContext;
import xyz.gnarbot.gnar.commands.template.annotations.Description;
import xyz.gnarbot.gnar.utils.Context;

import java.lang.reflect.Method;
import java.util.*;

public abstract class CommandTree<C extends DispatcherContext> extends CommandExecutor<C> implements Branch<C> {
    private final Map<String, Branch<C>> branches;

    public CommandTree() {
        this.branches = new LinkedHashMap<>();

        Method[] methods = getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Description.class)) continue;

            if (method.getParameterCount() == 0 || method.getParameters()[0].getType() != Context.class) {
                throw new RuntimeException("First argument of " + method + " must be Context");
            }

            MethodBranch annotation = method.getAnnotation(MethodBranch.class);
            String name = annotation == null ? method.getName() : annotation.name();

            String[] parts = name.split("_");

            Branch<C> current = this;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];

                Branch<C> next = current.getBranches().get(part);
                if (next == null) {
                    next = new PlaceholderBranch<>();
                    current.getBranches().put(part, next);
                }

                current = next;
            }

            current.addBranch(parts[parts.length - 1], new MethodLeaf<>(this, annotation, method));
        }
    }

    @Override
    public void execute(C context, String label, List<String> args) {
        walk(context, args);
    }

    @Override
    public Map<String, Branch<C>> getBranches() {
        return branches;
    }

    @Override
    public String description() {
        return getInfo().getDescription();
    }
}
