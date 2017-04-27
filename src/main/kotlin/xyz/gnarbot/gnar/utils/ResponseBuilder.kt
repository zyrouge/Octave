package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.requests.RestAction
import xyz.gnarbot.gnar.BotConfiguration
import java.awt.Color

class ResponseBuilder(val channel: MessageChannel) {
    /**
     * Quick-reply to a message.
     *
     * @param text The text to send.
     * @return The Message created by this function.
     */
    fun text(text: String): RestAction<Message> {
        return channel.sendMessage(MessageBuilder().append(text).build())
    }

    /**
     * Send a standard info message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun info(msg: String): RestAction<Message> {
        return embed {
            title = "Info"
            description = msg
            color = BotConfiguration.ACCENT_COLOR
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
            title = "Error"
            description = msg
            color = Color.RED
        }.action()
    }

    /**
     * Send a standard exception message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun exception(exception: Exception): RestAction<Message> {
        return embed {
            title = "Exception"
            description = exception.message
            color = Color.RED
        }.action()
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    @JvmOverloads
    fun embed(title: String? = null): ResponseEmbedBuilder = ResponseEmbedBuilder(channel).apply {
        this.title = title
        this.color = BotConfiguration.ACCENT_COLOR
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

    /**
     * A specialized [AbstractEmbedBuilder] that can use [action] to quickly send an embed message.
     */
    class ResponseEmbedBuilder(val channel: MessageChannel) : AbstractEmbedBuilder<ResponseEmbedBuilder>() {
        fun action(): RestAction<Message> = channel.sendMessage(build())
    }
}

