package xyz.gnarbot.gnar.commands.music

import com.jagrosh.jdautilities.paginator
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = ["cleanup", "cu"],
        description = "Clear songs queued by a certain user"
)
@BotInfo(
        id = 88,
        category = Category.MUSIC,
        roleRequirement = "DJ"
)
class CleanupCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = context.bot.players.getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        if (context.message.mentionedUsers.size == 0) {
            context.send().error("You must mention a user to purge silly!").queue()
            return
        }

        val queue = manager.scheduler.queue

        val purgeUser = context.message.mentionedUsers[0].idLong
        var total = 0

        val removeSongs = ArrayList<AudioTrack>()
        for (song in queue) {
            if(song.getUserData(TrackContext::class.java)?.requester == purgeUser) {
                removeSongs.add(song)
                total++
            }

        }
        if(removeSongs.size > 0) queue.removeAll(removeSongs)

        context.send().info("Removed $total songs from ${context.message.mentionedUsers[0].name} from the queue").queue()
    }
}
