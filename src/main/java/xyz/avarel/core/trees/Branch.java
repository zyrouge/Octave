package xyz.avarel.core.trees;

import xyz.avarel.core.DispatcherContext;

import java.util.List;
import java.util.Map;

public interface Branch<C extends DispatcherContext> {
    Map<String, Branch<C>> getBranches();

    default void addBranch(String name, Branch<C> branch) {
        getBranches().put(name, branch);
    }

    default String description() {
        return getBranches().keySet().toString();
    }

    default void walk(C context, List<String> args) {
        walk(context, args, 0);
    }

    default void walk(C context, List<String> args, int depth) {
        walk(this, context, args, depth);
    }

    default void walk(Branch<C> origin, C context, List<String> args, int depth) {
        if (getBranches().isEmpty()) return;

        Branch<C> branch = getBranches().get(args.get(0));

        if (branch != null) {
            branch.walk(origin, context, args.subList(1, args.size()), depth + 1);
            return;
        }

        origin.onWalkFail(this, context, args, depth);
    }
    
    default void onWalkFail(Branch<C> failPoint, C context, List<String> args, int depth) {
        StringBuilder sb = new StringBuilder().append("Invalid argument number #").append(depth);

        if (!args.isEmpty()) {
            sb.append(": ").append(args.get(0));
        }

        sb.append('\n').append(failPoint.description());

        throw new IllegalArgumentException(sb.toString());
    }
}