package xyz.avarel.core;

import xyz.avarel.core.impl.CommandDispatcherImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface CommandDispatcher<C> {
    void dispatch(C context);

    final class Builder<C extends DispatcherContext> {
        private final CommandRegistry<C> registry;
        private ExecutorService executor;
        private BiFunction<C, String, String> prefixStripper;
        private Function<String, List<String>> argumentSplitter;
        private List<BiPredicate<CommandExecutor<C>, C>> predicates;

        public Builder(CommandRegistry<C> registry) {
            this.registry = registry;
            this.predicates = new ArrayList<>();
        }

        public Builder<C> setExecutorService(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder<C> setPrefixStripper(BiFunction<C, String, String> prefixStripper) {
            this.prefixStripper = prefixStripper;
            return this;
        }


        public Builder<C> setArgumentSplitter(Function<String, List<String>> argumentSplitter) {
            this.argumentSplitter = argumentSplitter;
            return this;
        }

        public Builder<C> addPredicate(BiPredicate<CommandExecutor<C>, C> predicate) {
            this.predicates.add(predicate);
            return this;
        }

        public CommandDispatcher<C> build() {
            return new CommandDispatcherImpl<>(registry, executor, prefixStripper, argumentSplitter, predicates);
        }
    }
}
