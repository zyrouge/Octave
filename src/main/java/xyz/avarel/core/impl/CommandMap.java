package xyz.avarel.core.impl;

import xyz.avarel.core.CommandExecutor;
import xyz.avarel.core.CommandRegistry;
import xyz.avarel.core.DispatcherContext;

import java.util.HashMap;
import java.util.Map;

public class CommandMap<C extends DispatcherContext> implements CommandRegistry<C> {
    private final Map<String, CommandExecutor<C>> map;

    public CommandMap() {
        this(new HashMap<>());
    }

    public CommandMap(Map<String, CommandExecutor<C>> map) {
        this.map = map;
    }

    @Override
    public CommandExecutor<C> getCommand(String label) {
        return map.get(label);
    }
}
