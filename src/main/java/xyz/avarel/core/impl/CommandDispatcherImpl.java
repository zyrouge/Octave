package xyz.avarel.core.impl;

import xyz.avarel.core.CommandDispatcher;
import xyz.avarel.core.CommandExecutor;
import xyz.avarel.core.CommandRegistry;
import xyz.avarel.core.DispatcherContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CommandDispatcherImpl<C extends DispatcherContext> implements CommandDispatcher<C> {
    private final CommandRegistry<C> registry;
    private final Executor service;
    private final BiFunction<C, String, String> prefixStripper;
    private final Function<String, List<String>> argumentSplitter;
    private final List<BiPredicate<CommandExecutor<C>, C>> predicates;

    public CommandDispatcherImpl(CommandRegistry<C> registry,
                                 Executor service,
                                 BiFunction<C, String, String> prefixStripper,
                                 Function<String, List<String>> argumentSplitter,
                                 List<BiPredicate<CommandExecutor<C>, C>> predicates) {
        this.registry = registry;
        this.service = service;
        this.prefixStripper = prefixStripper;
        this.argumentSplitter = argumentSplitter;
        this.predicates = predicates;
    }

    public CommandRegistry<C> getRegistry() {
        return registry;
    }

    @Override
    public void dispatch(C context) {
        if (service != null) service.execute(() -> dispatchContext(context));
        else dispatchContext(context);
    }

    public void dispatchContext(C context) {
        String content = context.getContent();

        if (prefixStripper != null) content = prefixStripper.apply(context, content);
        if (content == null) return;


        List<String> split;
        if (argumentSplitter != null) split = argumentSplitter.apply(content);
        else split = Arrays.asList(content.split("\\s+"));
        if (split == null || split.isEmpty()) return;

        CommandExecutor<C> cmd = registry.getCommand(split.get(0));
        if (cmd == null) return;

        for (BiPredicate<CommandExecutor<C>, C> predicate : predicates) {
            if (!predicate.test(cmd, context)) return;
        }

        cmd.execute(context, split.get(0), Collections.unmodifiableList(split.subList(1, split.size())));
    }
}