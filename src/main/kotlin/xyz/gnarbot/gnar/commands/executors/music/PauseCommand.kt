package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 68,
        aliases = ["pause"],
        description = "Pause or resume the music player.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class PauseCommand : MusicCommandExecutor(true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        manager.player.isPaused = !manager.player.isPaused

        context.send().embed("Playback Control") {
            desc {
                if (manager.player.isPaused) {
                    "The player has been paused."
                } else {
                    "The player has resumed playing."
                }
            }
        }.action().queue()
    }
}
