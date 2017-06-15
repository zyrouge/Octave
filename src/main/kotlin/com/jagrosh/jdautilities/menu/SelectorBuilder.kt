package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.core.entities.Message

class SelectorBuilder(waiter: EventWaiter) : MenuBuilder<SelectorBuilder>(waiter) {
    private val options: MutableList<Selector.Entry> = mutableListOf()

    fun addOption(option: String, action: (Message) -> Unit): SelectorBuilder {
        options.add(Selector.Entry(option, action))
        return this
    }

    override fun build(): Selector {
        return Selector(waiter, user, title, description, color, options, timeout, unit, finally)
    }
}