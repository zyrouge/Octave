package xyz.gnarbot.gnar.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.TrackContext

@Command(
        aliases = ["cleanup", "cu"],
        description = "Clear songs queued by a certain user"
)
@BotInfo(
        id = 88,
        category = Category.MUSIC,
        djLock = true
)
class CleanupCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val manager = context.bot.players.getExisting(context.guild)
        if (manager == null) {
            context.send().issue("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        if (context.message.mentionedUsers.isEmpty() && args.isEmpty()) {
            context.send().issue("You must mention a user to purge their queue items, or use `cleanup left` to remove songs from users who left.").queue()
            return
        }

        val purgeUser = if (context.message.mentionedUsers.isNotEmpty())
            context.message.mentionedUsers[0].idLong else args[0]

        val queue = manager.scheduler.queue
        val removeSongs = ArrayList<AudioTrack>() //Prevent Concurrent Modification Exception

        for (song in queue) {
            if(purgeUser != "left") {
                if (song.getUserData(TrackContext::class.java)?.requester == purgeUser) {
                    removeSongs.add(song)
                }
            } else {
                try {
                    if (context.guild.getMemberById(song.getUserData(TrackContext::class.java)!!.requester)?.voiceState?.channel?.idLong
                            != context.guild.selfMember.voiceState?.channel?.idLong) { //there seriously HAS to be a better way to do this what the fuck
                        removeSongs.add(song)
                    }
                } catch(e: Exception) { //User kicked or banned will result in above erroring out
                    removeSongs.add(song)

                    e.printStackTrace()
                }
            }

        }
        if(removeSongs.size > 0) queue.removeAll(removeSongs)

        if(purgeUser == "left") {
            if(removeSongs.size == 0) {
                context.send().error("There are no songs to clear.")
                return
            }
            context.send().info("Removed ${removeSongs.size} songs from users no longer in voice channel.").queue()
        } else {
            context.send().info("Removed ${removeSongs.size} songs from user ${context.message.mentionedUsers[0].name} from the queue").queue()
        }
    }
}
