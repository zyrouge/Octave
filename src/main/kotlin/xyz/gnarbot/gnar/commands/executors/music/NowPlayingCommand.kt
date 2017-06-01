package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.inlineCode
import xyz.gnarbot.gnar.utils.link

@Command(
        aliases = arrayOf("nowplaying", "np"),
        description = "Shows what's currently playing.",
        category = Category.MUSIC
)
class NowPlayingCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val track = context.guildData.musicManager.player.playingTrack

        if (track == null) {
            context.send().error("The player is not currently playing anything.").queue()
            return
        }

        context.send().embed("Now Playing") {
            color = context.bot.config.musicColor

            field("Track", false) {
                "**[${track.info.title}](${track.info.uri})**"
            }

            val position = Utils.getTimestamp(track.position)
            val duration = Utils.getTimestamp(track.duration)

            field("Progress", true) {
                val percent = track.position.toDouble() / track.duration
                buildString {
                    for (i in 0 until 10) {
                        if (i / 10.toDouble() > percent) {
                            append("\u25AC")
                        } else {
                            append("\u25AC" link "")
                        }
                    }
                    append(" **%.1f**%%".format(percent * 100))
                }
            }
            field("Time", true) {
                inlineCode { "[$position / $duration]" }
            }
        }.action().queue()
    }
}
