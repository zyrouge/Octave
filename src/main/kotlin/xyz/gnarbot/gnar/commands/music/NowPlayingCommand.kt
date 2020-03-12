package xyz.gnarbot.gnar.commands.music

import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = ["nowplaying", "np", "playing"],
        description = "Shows what's currently playing."
)
@BotInfo(
        id = 67,
        category = Category.MUSIC
)
class NowPlayingCommand : MusicCommandExecutor(false, true, true) {
    private val totalBlocks = 20

    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val track = manager.player.playingTrack

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
                    "`Streaming`"
                } else {
                    val position = Utils.getTimestamp(track.position)
                    val duration = Utils.getTimestamp(track.duration)
                    "`[$position / $duration]`"
                }
            }

            field("Progress", true) {
                val percent = track.position.toDouble() / track.duration
                buildString {
                    append("[")
                    for (i in 0 until totalBlocks) {
                        if ((percent * (totalBlocks - 1)).toInt() == i) {
                            append("\u25AC")
                            append("]()")
                        } else {
                            append("\u2015")
                        }
                    }
                    append(" **%.1f**%%".format(percent * 100))
                }
            }

            footer { "Use `_lyrics current` to see the lyrics of the song!" }
        }.action().queue()
    }
}
