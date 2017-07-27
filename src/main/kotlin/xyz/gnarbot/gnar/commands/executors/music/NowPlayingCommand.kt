package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.inlineCode

@Command(
        id = 67,
        aliases = arrayOf("nowplaying", "np"),
        description = "Shows what's currently playing.",
        category = Category.MUSIC
)
class NowPlayingCommand : CommandExecutor() {
    private val totalBlocks = 20

    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        val track = manager.player.playingTrack

        if (track == null) {
            context.send().error("The player is not currently playing anything.\n$PLAY_MESSAGE").queue()
            return
        }

        context.send().embed("Now Playing") {
            desc {
                "**[${track.info.embedTitle}](${track.info.uri})**"
            }

            manager.discordFMTrack?.let {
                field("Discord.FM") {
                    val member = context.guild.getMemberById(it.requester)
                    buildString {
                        append("Currently streaming music from Discord.FM station `${it.station.capitalize()}`")
                        member?.let {
                            append(", requested by ${member.asMention}")
                        }
                        append('.')
                    }
                }
            }

            field("Requester", true) {
                track.getUserData(TrackContext::class.java)?.requester?.let {
                    context.guild.getMemberById(it)?.asMention
                } ?: "Not Found"
            }

            field("Request Channel", true) {
                track.getUserData(TrackContext::class.java)?.requestedChannel?.let {
                    context.guild.getTextChannelById(it)?.asMention
                } ?: "Not Found"
            }

            field("Repeating", true) {
                manager.scheduler.repeatOption
            }

            field("Time", true) {
                if (track.duration == Long.MAX_VALUE) {
                    inlineCode { "Streaming" }
                } else {
                    val position = Utils.getTimestamp(track.position)
                    val duration = Utils.getTimestamp(track.duration)
                    inlineCode { "[$position / $duration]" }
                }
            }

            field("Progress", true) {
                val percent = track.position.toDouble() / track.duration
                buildString {
                    append("[")
                    for (i in 0..totalBlocks - 1) {
                        if ((percent * (totalBlocks - 1)).toInt() == i) {
                            append("\u25AC")
                            append("]()")
                        } else {
                            append("\u25AC")
                        }
                    }
                    append(" **%.1f**%%".format(percent * 100))
                }
            }
        }.action().queue()
    }
}
