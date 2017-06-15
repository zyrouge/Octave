package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("reset"),
        description = "Completely reset the music player.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL)
)
class ResetCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.guildData.musicManager.reset()

        context.send().embed("Reset Music") {
            setColor(Bot.CONFIG.musicColor)
            setDescription("The player was completely reset.")
        }.action().queue()
    }
}
