package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.IEventManager
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AsyncEventManager(private val executor: ExecutorService = AsyncEventManager.POOL) : IEventManager {
    companion object {
        @JvmStatic val POOL: ExecutorService by lazy {
            Executors.newCachedThreadPool {
                Thread(it, "EventThread").apply {
                    isDaemon = true
                }
            }
        }
    }

    private val listeners = CopyOnWriteArraySet<EventListener>()

    override fun handle(event: Event?) {
        executor.execute {
            listeners.forEach {
                try {
                    it.onEvent(event)
                }
                catch (ex: Throwable) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun register(listener: Any?) {
        require(listener is EventListener) {
            "Listener must implement EventListener!"
        }
        listeners += listener as EventListener
    }

    override fun getRegisteredListeners(): MutableList<Any> = mutableListOf(listeners)

    override fun unregister(listener: Any?) {
        if (listener is EventListener)
            listeners -= listener
    }
}