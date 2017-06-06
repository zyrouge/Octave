package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.guilds.GuildData

/**
 * Contains
 */
data class Context(val event: GuildMessageReceivedEvent, val guildData: GuildData, val shard: Shard, val bot: Bot) {
    val keys = bot.keys
    val config = bot.config
    val log = bot.log

    val message: Message = event.message
    val channel: MessageChannel = event.channel
    val guild : Guild = event.guild

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