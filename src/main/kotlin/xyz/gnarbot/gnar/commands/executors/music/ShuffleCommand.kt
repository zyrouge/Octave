package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("shuffle"),
        description = "Shuffle the music queue.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class ShuffleCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to start playing some music!").queue()
            return
        }

        if (manager.scheduler.queue.isEmpty()) {
            context.send().error("The queue is empty.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to add some music!").queue()
            return
        }

        manager.scheduler.shuffle()

        context.send().embed("Shuffle Queue") {
            desc { "Player has been shuffled" }
        }.action().queue()
    }
}
