package xyz.gnarbot.gnar.commands.executors.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context
import java.util.*

@Command(
        id = 79,
        aliases = arrayOf("remove"),
        description = "Remove a song from the queue.",
        usage = "(first|last|all|#)",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class RemoveCommand : MusicCommandExecutor(true, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val queue = manager.scheduler.queue as LinkedList<AudioTrack>

        if (queue.isEmpty()) {
            context.send().error("The queue is empty.").queue()
            return
        }

        val track = when(args[0]) {
            "first" -> queue.removeFirst()
            "last" -> queue.removeLast()
            "all" -> {
                queue.clear()

                context.send().embed("Queue Clear") {
                    desc { "Cleared the music queue." }
                }.action().queue()

                return
            }
            else -> {
                val num = args[0].toIntOrNull()

                if (num == null || num !in 1..queue.size) {
                    context.send().error("That is not a valid track number. Try `1..${queue.size}`, `first`, or `last`.").queue()
                    return
                }

                queue.removeAt(num - 1)
            }
        }

        context.send().embed("Remove Track") {
            desc {
                "Removed __[${track.info.embedTitle}](${track.info.uri})__ from the queue."
            }
        }.action().queue()
    }
}