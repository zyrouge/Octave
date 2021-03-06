package xyz.gnarbot.gnar.utils.response

import io.sentry.Sentry
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.RestAction
import xyz.gnarbot.gnar.utils.EmbedProxy
import java.awt.Color
import javax.annotation.CheckReturnValue

open class ResponseBuilder(private val channel: MessageChannel) {
    /**
     * Quick-reply to a message.
     *
     * @param text The text to send.
     * @return The Message created by this function.
     */
    open fun text(text: String): RestAction<Message> {
        return channel.sendMessage(text)
    }

    /**
     * Send a standard info message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    open fun info(msg: String): RestAction<Message> {
        return embed {
            desc { msg }
        }.action()
    }

    /**
     * Send a "minor issue" message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    open fun issue(msg: String): RestAction<Message> {
        return embed {
            title { "Something is wrong here..." }
            desc { msg }
            color { Color.YELLOW }
        }.action()
    }

    /**
     * Send a standard error message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    open fun error(msg: String): RestAction<Message> {
        return embed {
            title { "Error" }
            desc { msg }
            color { Color.RED }
        }.action()
    }

    /**
     * Send a standard exception message.
     *
     * @return The Message created by this function.
     */
    open fun exception(exception: Exception): RestAction<Message> {
        exception.printStackTrace()
        Sentry.capture(exception)

        return embed {
            title { "Exception" }
            desc {
                buildString {
                    append("We found an unknown error while processing this command.")
                }
            }
            color { Color.RED }
        }.action()
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     */
    open fun embed(): ResponseEmbedBuilder = embed(null)

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     *
     * @param title Title of the embed.
     */
    open fun embed(title: String?): ResponseEmbedBuilder = ResponseEmbedBuilder().apply {
        title { title }
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    inline fun embed(title: String? = null, value: ResponseEmbedBuilder.() -> Unit): ResponseEmbedBuilder {
        return embed(title).apply(value)
    }

    inner class ResponseEmbedBuilder : EmbedProxy<ResponseEmbedBuilder>() {
        @CheckReturnValue
        fun action(): RestAction<Message> {
            return channel.sendMessage(build())
        }
    }
}