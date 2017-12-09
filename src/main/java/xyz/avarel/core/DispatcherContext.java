package xyz.avarel.core;

public interface DispatcherContext {
    String getContent();
    CommandDispatcher<? extends DispatcherContext> getCommandDispatcher();
}