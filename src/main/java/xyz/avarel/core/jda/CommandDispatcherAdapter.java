package xyz.avarel.core.jda;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import xyz.avarel.core.CommandDispatcher;
import xyz.avarel.core.DispatcherContext;

import java.util.function.BiFunction;

public class CommandDispatcherAdapter<C extends DispatcherContext, E extends Event> implements EventListener {
    private final Class<E> cls;
    private final CommandDispatcher<C> dispatcher;
    private final BiFunction<CommandDispatcher<C>, E, C> transformer;

    public CommandDispatcherAdapter(Class<E> cls, CommandDispatcher<C> dispatcher, BiFunction<CommandDispatcher<C>, E, C> transformer) {
        this.cls = cls;
        this.dispatcher = dispatcher;
        this.transformer = transformer;
    }

    @Override
    public void onEvent(Event event) {
        if (cls.isInstance(event)) {
            dispatcher.dispatch(transformer.apply(dispatcher, cls.cast(event)));
        }
    }
}