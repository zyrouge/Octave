package com.jagrosh.jdautilities

import com.jagrosh.jdautilities.menu.Paginator
import com.jagrosh.jdautilities.menu.PaginatorBuilder
import com.jagrosh.jdautilities.menu.Selector
import com.jagrosh.jdautilities.menu.SelectorBuilder
import com.jagrosh.jdautilities.waiter.EventWaiter

inline fun EventWaiter.selector(action: SelectorBuilder.() -> Unit): Selector {
    return SelectorBuilder(this).apply(action).build()
}

inline fun EventWaiter.paginator(action: PaginatorBuilder.() -> Unit): Paginator {
    return PaginatorBuilder(this).apply(action).build()
}