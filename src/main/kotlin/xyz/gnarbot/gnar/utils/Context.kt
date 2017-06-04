package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.*
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.guilds.GuildData

/**
 * Contains
 */
data class Context(val message: Message, val channel: MessageChannel, val guild : Guild, val guildData: GuildData, val shard: Shard, val bot: Bot) {
    val keys = bot.keys
    val config = bot.config
    val log = bot.log

    val member: Member = message.member
    val user: User = message.author

    /**
     * Return a class with utilities to help send a message to
     * this message's channel.

     * @return Response builder.
     */
    @JvmOverloads
    fun send(channel: MessageChannel = message.channel) = ResponseBuilder(channel, bot)
}