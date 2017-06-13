package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import java.awt.Color
import java.util.concurrent.TimeUnit

class SelectorBuilder(val waiter: EventWaiter) {
    private var user: User? = null

    private var title: String = "Select an Option"
    private var description: String = "Select one of the following options."
    private var color: Color? = null

    private val options: MutableList<Selector.Entry> = mutableListOf()
    private var finally: (() -> Unit)? = null

    private var timeout: Long = 20
    private var unit: TimeUnit = TimeUnit.SECONDS

    fun setTitle(title: String): SelectorBuilder {
        this.title = title
        return this
    }

    fun setDescription(description: String): SelectorBuilder {
        this.description = description
        return this
    }

    fun setColor(color: Color): SelectorBuilder {
        this.color = color
        return this
    }

    fun setUser(user: User): SelectorBuilder {
        this.user = user
        return this
    }

    fun finally(action: () -> Unit) {
        this.finally = action
    }

    fun addOption(option: String, action: (Message) -> Unit): SelectorBuilder {
        options.add(Selector.Entry(option, action))
        return this
    }

    fun setTimeout(timeout: Long, unit: TimeUnit): SelectorBuilder {
        this.timeout = timeout
        this.unit = unit
        return this
    }

    fun build(): Selector {
        return Selector(waiter, user, title, description, color, options, timeout, unit)
    }
}