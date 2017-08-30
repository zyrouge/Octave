package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.executors.music.MusicCommandExecutor
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 60,
        aliases = arrayOf("forceskip"),
        description = "Skip the current music track forcefully.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.MANAGE_CHANNEL),
        roleBypass = "DJ"
)
class ForceSkipCommand : MusicCommandExecutor(false, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        manager.scheduler.nextTrack()

        context.send().embed("Skip Current Track") {
            desc { "The track was skipped." }
        }.action().queue()
    }
}
