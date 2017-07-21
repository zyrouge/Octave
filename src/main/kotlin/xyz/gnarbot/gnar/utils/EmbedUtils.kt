@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("EmbedUtils")
package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.EmbedBuilder

@JvmOverloads
fun embed(title: String? = null): EmbedMaker = EmbedMaker().apply {
    title { title }
}

inline fun embed(title: String? = null, value: EmbedMaker.() -> Unit): EmbedMaker {
    return embed(title).apply(value)
}

inline fun EmbedBuilder.field(inline: Boolean = false) = addBlankField(inline)

inline fun EmbedBuilder.field(name: String?, inline: Boolean = false, value: Any?): EmbedBuilder {
    addField(name, value.toString(), inline)
    return this
}

inline fun EmbedBuilder.field(name: String?, inline: Boolean = false, value: () -> Any?): EmbedBuilder {
    addField(name, value().toString(), inline)
    return this
}

inline fun EmbedBuilder.desc(value: () -> Any?): EmbedBuilder {
    this.description(value)
    return this
}

inline fun EmbedBuilder.description(value: () -> Any?): EmbedBuilder {
    setDescription(value().toString())
    return this
}

/** Bold strings.*/
fun b(string: String) = "**$string**"

/** Bold strings. */
fun b(any: Any?) = b(any.toString())

/** Italicize strings. */
fun i(string: String) = "*$string*"

/** Italicize strings. */
fun i(any: Any?) = i(any.toString())

/** Underline strings. */
fun u(string: String) = "__${string}__"

/** Underline strings. */
fun u(any: Any?) = u(any.toString())

/** Link strings to a URL. */
infix fun String.link(url: String) = "[$this]($url)"

/** Link strings to a URL. */
infix fun Any?.link(url: String) = toString() link url

inline fun code(language: String = "", code: () -> String) = "```$language\n${code()}```"

inline fun inlineCode(code: () -> String) = "`${code()}`"