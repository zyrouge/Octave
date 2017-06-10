package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.requests.RestAction
import xyz.gnarbot.gnar.Bot

class ResponseBuilder(val channel: MessageChannel, val bot: Bot) {
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
            color = bot.config.accentColor
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
            color = bot.config.errorColor
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
            color = bot.config.errorColor
        }.action()
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedMaker.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    @JvmOverloads
    fun embed(title: String? = null): ResponseEmbedMaker = ResponseEmbedMaker(channel).apply {
        this.title = title
        this.color = bot.config.accentColor
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedMaker.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    inline fun embed(title: String? = null, value: ResponseEmbedMaker.() -> Unit): ResponseEmbedMaker {
        return embed(title).apply(value)
    }

    /**
     * A specialized [AbstractEmbedMaker] that can use [action] to quickly send an embed message.
     */
    class ResponseEmbedMaker(val channel: MessageChannel) : AbstractEmbedMaker<ResponseEmbedMaker>() {
        fun action(): RestAction<Message> = channel.sendMessage(build())
    }
}

