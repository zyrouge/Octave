package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.requests.RestAction
import xyz.gnarbot.gnar.Bot
import java.awt.Color
import java.time.temporal.TemporalAccessor

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
            setTitle("Info")
            setDescription(msg)
            setColor(bot.config.accentColor)
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
            setColor(bot.config.errorColor)
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
            setColor(bot.config.errorColor)
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
        setTitle(title)
        setColor(bot.config.accentColor)
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
    class ResponseEmbedBuilder(val channel: MessageChannel) : EmbedBuilder() {
        inline fun field(inline: Boolean = false) = addBlankField(inline)

        inline fun field(name: String?, inline: Boolean = false, value: Any?): ResponseEmbedBuilder {
            super.addField(name, value.toString(), inline)
            return this
        }

        inline fun field(name: String?, inline: Boolean = false, value: () -> Any?): ResponseEmbedBuilder {
            super.addField(name, value().toString(), inline)
            return this
        }

        inline fun description(value: () -> Any?): ResponseEmbedBuilder {
            setDescription(value().toString())
            return this
        }

        override fun setImage(url: String?): ResponseEmbedBuilder {
            super.setImage(url)
            return this
        }

        override fun clearFields(): ResponseEmbedBuilder {
            super.clearFields()
            return this
        }

        override fun appendDescription(description: CharSequence?): ResponseEmbedBuilder {
            super.appendDescription(description)
            return this
        }

        override fun setAuthor(name: String?, url: String?, iconUrl: String?): ResponseEmbedBuilder {
            super.setAuthor(name, url, iconUrl)
            return this
        }

        override fun setTimestamp(temporal: TemporalAccessor?): ResponseEmbedBuilder {
            super.setTimestamp(temporal)
            return this
        }

        override fun setDescription(description: CharSequence?): ResponseEmbedBuilder {
            super.setDescription(description)
            return this
        }

        override fun setColor(color: Color?): ResponseEmbedBuilder {
            super.setColor(color)
            return this
        }

        override fun setFooter(text: String?, iconUrl: String?): ResponseEmbedBuilder {
            super.setFooter(text, iconUrl)
            return this
        }

        override fun setTitle(title: String?): ResponseEmbedBuilder {
            super.setTitle(title)
            return this
        }

        override fun setTitle(title: String?, url: String?): ResponseEmbedBuilder {
            super.setTitle(title, url)
            return this
        }

        override fun setThumbnail(url: String?): ResponseEmbedBuilder {
            super.setThumbnail(url)
            return this
        }

        override fun addField(field: MessageEmbed.Field?): ResponseEmbedBuilder {
            super.addField(field)
            return this
        }

        override fun addField(name: String?, value: String?, inline: Boolean): ResponseEmbedBuilder {
            super.addField(name, value, inline)
            return this
        }

        override fun addBlankField(inline: Boolean): ResponseEmbedBuilder {
            super.addBlankField(inline)
            return this
        }

        fun action(): RestAction<Message> = channel.sendMessage(build())
    }
}

