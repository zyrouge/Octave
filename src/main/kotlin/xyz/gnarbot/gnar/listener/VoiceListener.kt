package xyz.gnarbot.gnar.listener

import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.LoadState
import xyz.gnarbot.gnar.utils.ResponseBuilder

class VoiceListener : ListenerAdapter() {
    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (Bot.STATE == LoadState.COMPLETE) {
            // I just joined the channel
            if (event.member.user == event.jda.selfUser) {
                return
            }

            val guild = event.guild ?: return

            Bot.getPlayers().getExisting(guild.idLong)?.let {
                if (!it.guild.selfMember.voiceState.inVoiceChannel()) {
                    Bot.getPlayers().destroy(guild.idLong)
                } else if (it.isAlone()) {
                    it.queueLeave()
                } else {
                    it.cancelLeave()
                }
            }
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (Bot.STATE == LoadState.COMPLETE) {
            // If the bot left the channel, destroy player.
            if (event.member.user == event.jda.selfUser) {
                Bot.getPlayers().destroy(event.guild.idLong)
                // else it should be handled by GuildVoiceMoveEvent
                return
            }

            val guild = event.guild ?: return

            Bot.getPlayers().getExisting(guild.idLong)?.let {
                if (!it.guild.selfMember.voiceState.inVoiceChannel()) {
                    Bot.getPlayers().destroy(guild.idLong)
                } else if (it.isAlone()) {
                    it.queueLeave()
                }
            }
        }
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (Bot.STATE == LoadState.COMPLETE) {
            if (event.member.user == event.jda.selfUser) {
                if (!event.guild.selfMember.voiceState.inVoiceChannel()) {
                    Bot.getPlayers().destroy(event.guild.idLong)
                    return
                }

                if (event.channelJoined?.id == event.guild.afkChannel?.id) {
                    Bot.getPlayers().destroy(event.guild.idLong)
                    return
                }

                Bot.getPlayers().getExisting(event.guild.idLong)?.let {
                    val options = Bot.getOptions().ofGuild(event.guild)
                    if (options.music.channels.isNotEmpty()) {
                        if (event.channelJoined.id !in options.music.channels) {
                            it.currentRequestChannel?.let { requestChannel ->
                                ResponseBuilder(requestChannel).error(
                                    "Can not join `${event.channelJoined.name}`, it isn't one of the designated music channels."
                                ).queue()
                            }

                            Bot.getPlayers().destroy(event.guild.idLong)
                            return
                        }
                    }

                    it.moveAudioConnection(event.channelJoined)

                    if (it.isAlone()) {
                        it.queueLeave()
                    } else {
                        it.cancelLeave()
                    }
                }
                return
            }

            val guild = event.guild ?: return

            Bot.getPlayers().getExisting(guild.idLong)?.let {
                if (!event.guild.selfMember.voiceState.inVoiceChannel()) {
                    Bot.getPlayers().destroy(event.guild.idLong)
                    return
                } else if (it.isAlone()) {
                    it.queueLeave()
                } else {
                    it.cancelLeave()
                }
            }
        }
    }
}