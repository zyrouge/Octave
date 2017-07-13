package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.inlineCode
import xyz.gnarbot.gnar.utils.link

@Command(
        id = 67,
        aliases = arrayOf("nowplaying", "np"),
        description = "Shows what's currently playing.",
        category = Category.MUSIC
)
class NowPlayingCommand : CommandExecutor() {
    private val totalBlocks = 10

    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        val track = manager.player.playingTrack

        if (track == null) {
            context.send().error("The player is not currently playing anything.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        context.send().embed("Now Playing") {
            desc {
                "**[${track.info.title}](${track.info.uri})**"
            }

            val position = Utils.getTimestamp(track.position)
            val duration = Utils.getTimestamp(track.duration)

            field("Time", true) {
                inlineCode { "[$position / $duration]" }
            }

            field("Repeating", true) {
                manager.scheduler.repeatOption
            }

            field("Progress", true) {
                val percent = track.position.toDouble() / track.duration
                buildString {
                    for (i in 0 until totalBlocks) {
                        if (i / totalBlocks.toDouble() > percent) {
                            append("\u25AC")
                        } else {
                            append("\u25AC" link "")
                        }
                    }
                    append(" **%.1f**%%".format(percent * 100))
                }
            }
        }.action().queue()
    }
}
