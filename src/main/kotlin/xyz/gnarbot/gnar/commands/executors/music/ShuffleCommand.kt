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
        if (context.guildData.musicManager.scheduler.queue.isEmpty()) {
            context.send().error("The queue is empty.\n" +
                    "\uD83C\uDFB6` _play (song/url)` to add some music!").queue()
            return
        }

        context.guildData.musicManager.scheduler.shuffle()

        context.send().embed("Shuffle Queue") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("Player has been shuffled")
        }.action().queue()
    }
}
