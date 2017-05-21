package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

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

            field("Now Playing", false, "__[${track.info.title}](${track.info.uri})__")

            val position = Utils.getTimestamp(track.position)
            val duration = Utils.getTimestamp(track.duration)

            field("Time", true, "**[$position / $duration]**")
        }.action().queue()
    }
}
