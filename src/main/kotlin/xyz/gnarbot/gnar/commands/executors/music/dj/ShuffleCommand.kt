package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("shuffle"),
        description = "Shuffle the music queue.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class ShuffleCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.guildData.musicManager.scheduler.shuffle()

        context.send().embed("Shuffle Queue") {
            setColor(context.bot.config.musicColor)
            description { "Player has been shuffled" }
        }.action().queue()
    }
}
