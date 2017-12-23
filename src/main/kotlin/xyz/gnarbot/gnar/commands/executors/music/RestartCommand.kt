package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["restart"],
        description = "Restart the current song."
)
@BotInfo(
        id = 71,
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RestartCommand : MusicCommandExecutor(true, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val track = manager.player.playingTrack ?: manager.scheduler.lastTrack

        if (track != null) {
            context.send().info("Restarting track: `${track.info.embedTitle}`.").queue()

            manager.player.playTrack(track.makeClone())
        } else {
            context.send().error("No track has been previously started.").queue()
        }
    }
}
