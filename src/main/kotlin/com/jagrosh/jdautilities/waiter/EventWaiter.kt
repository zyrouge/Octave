package com.jagrosh.jdautilities.waiter

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * A simple object used primarily for entities found in [com.jagrosh.jdautilities.menu].
 *
 * <p>The EventWaiter is capable of handling specialized forms of [Event]
 * that must meet criteria not normally specifiable without implementation of an [EventListener].
 *
 * <p>If you intend to use the EventWaiter, it is highly recommended you <b>DO NOT create multiple EventWaiters</b>!
 * Doing this will cause unnecessary increases in memory usage.
 *
 * @author John Grosh (jagrosh)
 * @author Avarel
 */
class EventWaiter : EventListener {
    private val waiters = mutableMapOf<Class<*>, MutableList<Waiter<Event>>>()

    @Suppress("UNCHECKED_CAST")
    fun <T: Event> waitForEvent(cls: Class<in T>,
                                predicate: (T) -> Boolean,
                                action: (T) -> Unit,
                                timeout: Long,
                                unit: TimeUnit?,
                                timeoutAction: (() -> Unit)?): Waiter<T> {
        val list = waiters.getOrPut(cls, ::mutableListOf)

        val waiter = Waiter(cls, predicate, action)
        list.add(waiter as Waiter<Event>)

        if (timeout > 0) {
            requireNotNull(unit)

            launch(CommonPool) {
                delay(timeout, unit!!)
                if (list.remove(waiter)) {
                    timeoutAction?.invoke()
                }
            }
        }

        return waiter
    }

    @Suppress("UNCHECKED_CAST")
    override fun onEvent(event: Event) {
        var cls: Class<in Event> = event.javaClass

        while (cls.superclass != null) {
            if (cls in waiters) {
                waiters[cls]?.removeIf { it.attempt(event) }
            }

            cls = cls.superclass
        }
    }

    fun <T: Event> waitFor(cls: Class<T>, action: (T) -> Unit) : WaiterBuilder<T> = WaiterBuilder(cls, action)

    fun <T: Event> waitFor(cls: Class<T>, action: Consumer<T>) : WaiterBuilder<T> = WaiterBuilder(cls) { action.accept(it) }

    // builder
    inner class WaiterBuilder<T: Event>(private var cls: Class<T>, private var action: (T) -> Unit) {
        private var predicate: ((T) -> Boolean) = { true }

        fun predicate(predicate: (event: T) -> Boolean): WaiterBuilder<T> {
            this.predicate = predicate
            return this
        }

        fun noTimeout(): Waiter<T> {
            return waitForEvent(cls, predicate, action, 0, null, null)
        }

        fun timeout(timeout: Long, unit: TimeUnit, timeoutAction: () -> Unit): Waiter<T> {
            return waitForEvent(cls, predicate, action, timeout, unit, timeoutAction)
        }

        fun timeout(timeout: Long, unit: TimeUnit, timeoutAction: Runnable): Waiter<T> {
            return waitForEvent(cls, predicate, action, timeout, unit, { timeoutAction.run() })
        }

        fun timeout(timeout: Long, unit: TimeUnit): Waiter<T> {
            return waitForEvent(cls, predicate, action, timeout, unit, null)
        }
    }

    inner class Waiter<in T: Event>(private val cls: Class<in T>,
                                    private val predicate: (T) -> Boolean,
                                    private val action: (T) -> Unit) {
        fun isValid(): Boolean {
            return waiters[cls]?.contains(this) == true
        }

        fun attempt(event: T): Boolean {
            return if (predicate(event)) {
                action(event)
                true
            } else {
                false
            }
        }

        fun cancel(): Boolean {
            return waiters[cls]?.remove(this) == true
        }
    }
}