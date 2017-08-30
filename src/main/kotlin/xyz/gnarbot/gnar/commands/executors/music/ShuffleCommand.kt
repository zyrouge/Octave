package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 72,
        aliases = arrayOf("shuffle"),
        description = "Shuffle the music queue.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class ShuffleCommand : MusicCommandExecutor(true, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (manager.scheduler.queue.isEmpty()) {
            context.send().error("The queue is empty.\n$PLAY_MESSAGE").queue()
            return
        }

        manager.scheduler.shuffle()

        context.send().embed("Shuffle Queue") {
            desc { "Player has been shuffled" }
        }.action().queue()
    }
}
