package xyz.gnarbot.gnar.tests;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

public class EventPublisher<T extends Event> extends SubmissionPublisher<T> implements EventListener {
    private final Class<T> eventClass;

    public EventPublisher(Class<T> eventClass) {
        super();
        this.eventClass = eventClass;
    }

    public EventPublisher(Class<T> eventClass, Executor executor, int maxBufferCapacity,
                          BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> handler) {
        super(executor, maxBufferCapacity, handler);
        this.eventClass = eventClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(Event event) {
        if (eventClass.isInstance(event)) {
            submit((T) event);
        }
    }
}
