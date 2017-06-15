package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.guilds.GuildData

/**
 * Contains
 */
data class Context(val event: GuildMessageReceivedEvent, val guildData: GuildData, val shard: Shard) {
    val message: Message = event.message
    val channel: TextChannel = event.channel
    val guild : Guild = event.guild

    val member: Member = message.member
    val user: User = message.author

    /**
     * Return a class with utilities to help send a message to
     * this message's channel.
     *
     * @return Response builder.
     */
    fun send() = ResponseBuilder(this)
}