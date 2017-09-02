package xyz.gnarbot.gnar.utils.response

import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.TextChannel

fun PrivateChannel.respond(): ResponseBuilder = ResponseBuilder(this)

fun TextChannel.respond(): ResponseBuilder = GuildResponseBuilder(this)
