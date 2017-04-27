package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("repeat"),
        description = "Set if the music player should repeat.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class RepeatCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = context.guildData.musicManager

        manager.scheduler.isRepeating = !manager.scheduler.isRepeating

        context.send().embed("Repeat Queue") {
            color = BotConfiguration.MUSIC_COLOR
            description = "Music player was set to __${if (manager.scheduler.isRepeating) "repeat" else "not repeat"}__."
        }.action().queue()
    }
}
