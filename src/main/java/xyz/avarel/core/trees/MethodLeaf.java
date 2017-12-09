package xyz.avarel.core.trees;

import xyz.avarel.core.DispatcherContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class MethodLeaf<C extends DispatcherContext> implements Branch<C> {
    private final CommandTree<C> command;
    private final MethodBranch branch;
    private final Method method;

    public MethodLeaf(CommandTree<C> command, MethodBranch branch, Method method) {
        this.command = command;
        this.branch = branch;
        this.method = method;
    }

    @Override
    public Map<String, Branch<C>> getBranches() {
        return Collections.emptyMap();
    }

    @Override
    public String description() {
        return null;
    }
}
