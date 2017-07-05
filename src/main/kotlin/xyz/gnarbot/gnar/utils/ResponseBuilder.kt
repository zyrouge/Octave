package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.requests.RestAction
import java.awt.Color

class ResponseBuilder(val context: Context) {
    /**
     * Quick-reply to a message.
     *
     * @param text The text to send.
     * @return The Message created by this function.
     */
    fun text(text: String): RestAction<Message> {
        return context.channel.sendMessage(text)
    }

    /**
     * Send a standard info message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun info(msg: String): RestAction<Message> {
        return embed {
            title { "Info" }
            desc  { msg }
            color { context.guild.selfMember.color }
        }.action()
    }

    /**
     * Send a standard error message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun error(msg: String): RestAction<Message> {
        return embed {
            title { "Error" }
            desc  { msg }
            color { Color.RED }
        }.action()
    }

    /**
     * Send a standard exception message.
     *
     * @return The Message created by this function.
     */
    fun exception(exception: Exception): RestAction<Message> {
        return error("${exception.javaClass.simpleName}: ${exception.message ?: ""}")
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    @JvmOverloads
    fun embed(title: String? = null): ResponseEmbedBuilder = ResponseEmbedBuilder().apply {
        title { title }
        color { context.guild.selfMember.color ?: Color.WHITE }
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    inline fun embed(title: String? = null, value: ResponseEmbedBuilder.() -> Unit): ResponseEmbedBuilder {
        return embed(title).apply(value)
    }

    @Suppress("NOTHING_TO_INLINE")
    inner class ResponseEmbedBuilder : EmbedProxy<ResponseEmbedBuilder>() {
        fun action(): RestAction<Message> {
            return context.channel.sendMessage(build())
        }
    }
}

