package xyz.gnarbot.gnar.commands.executors.music

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 73,
        aliases = arrayOf("skip"),
        description = "Skip the current music track if you're the requester.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class SkipCommand : MusicCommandExecutor(true, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (manager.player.playingTrack.getUserData(TrackContext::class.java)?.requester != context.member.user.idLong
                && !context.member.hasPermission(Permission.MANAGE_CHANNEL)) {
            context.send().error("You did not request this track.").queue()
            return
        }

        manager.scheduler.nextTrack()

        context.send().embed("Skip Current Track") {
            desc { "The track was skipped." }
        }.action().queue()
    }
}
