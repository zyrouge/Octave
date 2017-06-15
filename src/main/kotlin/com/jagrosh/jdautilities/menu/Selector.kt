package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import xyz.gnarbot.gnar.utils.embed
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color
import java.util.concurrent.TimeUnit

class Selector(val waiter: EventWaiter,
               val user: User?,
               val title: String,
               val description: String?,
               val color: Color?,
               val options: List<Entry>,
               val timeout: Long,
               val unit: TimeUnit,
               val finally: (Message?) -> Unit) {
    val cancel = "\u274C"

    var message: Message? = null

    fun display(channel: TextChannel) {
        if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EMBED_LINKS)) {
            channel.sendMessage(embed("Error") {
                description {
                    buildString {
                        append("The bot requires the permission `${Permission.MESSAGE_ADD_REACTION.getName()}`, ")
                        append("`${Permission.MESSAGE_MANAGE.getName()}` and ")
                        append("`${Permission.MESSAGE_EMBED_LINKS.getName()}` for selection menus.")
                    }
                }
            }.build()).queue()
            finally(message)
            return
        }

        channel.sendMessage(embed(title) {
            setDescription(description)
            setColor(color)
            field("Options") {
                buildString {
                    options.forEachIndexed { index, (name) ->
                        append("${'\u0030' + index}\u20E3 $name").ln()
                    }
                }
            }
            setFooter("This selection will time out in $timeout ${unit.toString().toLowerCase()}.", null)
        }.build()).queue {
            message = it
            options.forEachIndexed { index, _ ->
                it.addReaction("${'\u0030' + index}\u20E3").queue()
            }
            it.addReaction(cancel).queue()
        }

        waiter.waitFor(MessageReactionAddEvent::class.java) {
            if (it.reaction.emote.name == cancel) {
                finally(message)
                return@waitFor
            }

            val value = it.reaction.emote.name[0] - '\u0030'
            it.channel.getMessageById(it.messageIdLong).queue {
                options[value].action(it)
            }
            finally(message)
        }.predicate {
            when {
                it.messageIdLong != message?.idLong -> false
                it.user.isBot -> false
                user != null && it.user != user -> {
                    it.reaction.removeReaction(it.user).queue()
                    false
                }
                else -> {
                    if (it.reaction.emote.name == cancel) {
                        true
                    } else {
                        val value = it.reaction.emote.name[0] - '\u0030'
                        if (value in 0..options.size - 1) {
                            true
                        } else {
                            it.reaction.removeReaction(it.user).queue()
                            false
                        }
                    }
                }
            }
        }.timeout(timeout, unit) {
            finally(message)
        }
    }

    data class Entry(val name: String, val action: (Message) -> Unit)
}