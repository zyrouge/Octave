package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 71,
        aliases = ["restart"],
        description = "Restart the current song.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RestartCommand : MusicCommandExecutor(true, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val track = manager.player.playingTrack ?: manager.scheduler.lastTrack

        if (track != null) {
            context.send().embed("Restart Song") {
                desc { "Restarting track: `${track.info.embedTitle}`." }
            }.action().queue()

            manager.player.playTrack(track.makeClone())
        } else {
            context.send().error("No track has been previously started.").queue()
        }
    }
}
