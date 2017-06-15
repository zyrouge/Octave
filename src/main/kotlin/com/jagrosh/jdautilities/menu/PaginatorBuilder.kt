package com.jagrosh.jdautilities.menu

import com.google.common.collect.Lists
import com.jagrosh.jdautilities.waiter.EventWaiter

class PaginatorBuilder(waiter: EventWaiter) : MenuBuilder<PaginatorBuilder>(waiter) {
    private val items: MutableList<String> = mutableListOf()

    private var itemsPerPage = 10

    fun add(item: String): PaginatorBuilder {
        items.add(item)
        return this
    }

    fun setItemsPerPage(itemsPerPage: Int): PaginatorBuilder {
        this.itemsPerPage = itemsPerPage
        return this
    }

    override fun build(): Paginator {
        return Paginator(waiter, user, title, description, color, Lists.partition(items, itemsPerPage), timeout, unit, finally)
    }
}