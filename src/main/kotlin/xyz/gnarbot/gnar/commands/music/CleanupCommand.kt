package xyz.gnarbot.gnar.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import io.sentry.Sentry
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
            ?: return context.send().issue("There's no music player in this guild.\n$PLAY_MESSAGE").queue()

        if (context.message.mentionedUsers.isEmpty() && args.isEmpty()) {
            return context.send()
                .issue("You must mention a user to purge their queue items, or use `cleanup left` to remove songs from users who left.")
                .queue()
        }

        val purge = context.message.mentionedUsers.firstOrNull()?.id
            ?: args[0]

        val oldSize = manager.scheduler.queue.size

        when (purge) {
            "left" -> {
                // Return Boolean: True if track should be removed
                val predicate: (AudioTrack) -> Boolean = check@{
                    val req = context.guild.getMemberById(it.getUserData(TrackContext::class.java)!!.requester)
                        ?: return@check true

                    return@check req.voiceState?.channel?.idLong != context.guild.selfMember.voiceState?.channel?.idLong
                }
                manager.scheduler.queue.removeIf(predicate)
            }
            "duplicates", "d", "dupes" -> {
                val tracks = mutableSetOf<String>()
                // Return Boolean: True if track should be removed (could not add to set: already exists).
                val predicate: (AudioTrack) -> Boolean = { !tracks.add(it.identifier) }
                manager.scheduler.queue.removeIf(predicate)
            }
            else -> {
                val userId = purge.toLong()
                val predicate: (AudioTrack) -> Boolean = { it.getUserData(TrackContext::class.java)?.requester == userId }
                manager.scheduler.queue.removeIf(predicate)
            }
        }

        val newSize = manager.scheduler.queue.size
        val removed = oldSize - newSize

        when (purge) {
            "left" -> {
                if (removed == 0) {
                    return context.send().error("There are no songs to clear.").queue()
                }

                context.send().info("Removed $removed songs from users no longer in the voice channel.").queue()
            }
            "duplicates", "d", "dupes" -> {
                if (removed == 0) {
                    return context.send().error("There were no duplicates.").queue()
                }

                context.send().info("Removed $removed duplicate songs from the queue.").queue()
            }
            else -> {
                val user = context.guild.getMemberById(purge)?.user?.name ?: "Unknown User"
                context.send().info("Removed $removed songs queued by **$user**.").queue()
            }
        }
    }
}
