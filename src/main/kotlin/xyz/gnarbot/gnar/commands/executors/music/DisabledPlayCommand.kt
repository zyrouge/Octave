package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 62,
        aliases = arrayOf("play", "skip", "queue", "remove", "repeat", "np", "restart", "shuffle", "volume", "voteskip"),
        description = "Music is disabled TEMPORARILY."
)
class DisabledPlayCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().embed("Music Is Disabled") {
            desc {
                "For several reasons, music is **TEMPORARILY** disabled until the problem resolves."
            }
        }.action().queue()
    }
}