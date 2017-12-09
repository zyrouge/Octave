package xyz.avarel.core;

public interface CommandRegistry<C extends DispatcherContext> {
    CommandExecutor<C> getCommand(String label);
}
