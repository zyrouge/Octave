package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["shuffle"],
        description = "Shuffle the music queue."
)
@BotInfo(
        id = 72,
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
