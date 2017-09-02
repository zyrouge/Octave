package xyz.gnarbot.gnar.utils.response

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.requests.RestAction

class GuildResponseBuilder(channel: TextChannel): ResponseBuilder(channel) {
    val guild: Guild = channel.guild

    override fun info(msg: String): RestAction<Message> {
        return embed {
            title { "Info" }
            desc  { msg }
            color { guild.selfMember.color }
        }.action()
    }

    override fun embed(title: String?): ResponseEmbedBuilder = super.embed(title).apply {
        color { guild.selfMember.color }
    }
}