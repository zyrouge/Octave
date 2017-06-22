package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import xyz.gnarbot.gnar.utils.embed
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color
import java.util.concurrent.TimeUnit

class Selector(val waiter: EventWaiter,
               val type: Type,
               val user: User?,
               val title: String,
               val description: String?,
               val color: Color?,
               val options: List<Entry>,
               val timeout: Long,
               val unit: TimeUnit,
               val finally: (Message?) -> Unit) {
    enum class Type {
        REACTIONS,
        MESSAGE
    }

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

            when (type) {
                Type.REACTIONS -> {
                    appendDescription("\n**Pick a reaction corresponding to the options.**")
                }
                Type.MESSAGE -> {
                    appendDescription("\n**Type a number corresponding to the options. ie: `0` or `cancel`**")
                }
            }

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
            when (type) {
                Type.REACTIONS -> {
                    options.forEachIndexed { index, _ ->
                        it.addReaction("${'\u0030' + index}\u20E3").queue()
                    }
                    it.addReaction(cancel).queue()
                }
                Type.MESSAGE -> { /* pass */ }
            }
        }

        when(type) {
            Type.REACTIONS -> {
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
            Type.MESSAGE -> {
                waiter.waitFor(GuildMessageReceivedEvent::class.java) {
                    val content = it.message.content
                    if (content == "cancel") {
                        finally(message)
                        return@waitFor
                    }

                    val value = content.toIntOrNull() ?: return@waitFor
                    it.channel.getMessageById(it.messageIdLong).queue {
                        options[value].action(it)
                    }
                    finally(message)
                }.predicate {
                    when {
                        it.author.isBot -> false
                        user != null && it.author != user -> {
                            false
                        }
                        else -> {
                            val content = it.message.content
                            if (content == "cancel") {
                                true
                            } else {
                                val value = content.toIntOrNull() ?: return@predicate false
                                value in 0..options.size - 1
                            }
                        }
                    }
                }.timeout(timeout, unit) {
                    finally(message)
                }
            }
        }
    }

    data class Entry(val name: String, val action: (Message) -> Unit)
}