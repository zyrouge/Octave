package xyz.gnarbot.gnar.utils

import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import javax.annotation.Nonnull

class NonnullTLongObjectHashMap<T>(val map: TLongObjectHashMap<T>) : TLongObjectMap<T> by map {
    override fun put(key: Long, @Nonnull value: T): T = map.put(key, value!!)
}