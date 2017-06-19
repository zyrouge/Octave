package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.requests.RestAction
import xyz.gnarbot.gnar.Bot

class ResponseBuilder(val context: Context) {
    /**
     * Quick-reply to a message.
     *
     * @param text The text to send.
     * @return The Message created by this function.
     */
    fun text(text: String): RestAction<Message> {
        return if (context.channel.canTalk()) {
            context.channel.sendMessage(text)
        } else {
            context.user.openPrivateChannel().complete().sendMessage(text)
        }
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
            description { msg }
            setColor(Bot.CONFIG.accentColor)
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
            setTitle("Error")
            setDescription(msg)
            setColor(Bot.CONFIG.errorColor)
        }.action()
    }

    /**
     * Send a standard exception message.
     *
     * @return The Message created by this function.
     */
    fun exception(exception: Exception): RestAction<Message> {
        return embed {
            setTitle("Exception")
            setDescription(exception.message)
            setColor(Bot.CONFIG.errorColor)
        }.action()
    }

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br> This builder can use [ResponseEmbedBuilder.action] to quickly send the built embed.
     *
     * @param title Title of the embed.
     */
    @JvmOverloads
    fun embed(title: String? = null): ResponseEmbedBuilder = ResponseEmbedBuilder().apply {
        setTitle(title)
        setColor(Bot.CONFIG.accentColor)
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
            return if (context.guild.selfMember.hasPermission(context.channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
                context.channel.sendMessage(build())
            } else {
                context.user.openPrivateChannel().complete().sendMessage(build())
            }
        }
    }
}

