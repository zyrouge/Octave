@file:Suppress("NOTHING_TO_INLINE")
package xyz.gnarbot.gnar.utils

inline fun StringBuilder.ln(): StringBuilder = appendln()

inline fun Int.conformToRange(min: Int, max: Int) = Math.min(Math.max(min, this), max)