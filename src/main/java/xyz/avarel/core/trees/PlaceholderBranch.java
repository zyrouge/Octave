package xyz.avarel.core.trees;

import xyz.avarel.core.DispatcherContext;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlaceholderBranch<C extends DispatcherContext> implements Branch<C> {
    private final Map<String, Branch<C>> branches;

    public PlaceholderBranch() {
        this.branches = new LinkedHashMap<>();
    }

    @Override
    public Map<String, Branch<C>> getBranches() {
        return branches;
    }

    @Override
    public String description() {
        if (branches.isEmpty()) return "";

        StringBuilder builder = new StringBuilder(branches.size() * 16);
        for (Map.Entry<String, Branch<C>> cursor : branches.entrySet()) {
            builder.append("  - `").append(cursor.getKey()).append("` ");
            builder.append(cursor.getValue().description());
            builder.append('\n');
        }
        builder.substring(0, builder.length() - 1);

        return builder.toString();
    }
}
