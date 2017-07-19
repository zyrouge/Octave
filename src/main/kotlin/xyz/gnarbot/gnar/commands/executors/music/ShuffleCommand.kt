package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 72,
        aliases = arrayOf("shuffle"),
        description = "Shuffle the music queue.",
        category = Category.MUSIC,
        scope = Scope.VOICE
)
class ShuffleCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n$PLAY_MESSAGE").queue()
            return
        }

        val botChannel = context.guild.selfMember.voiceState.channel
        if (botChannel == null) {
            context.send().error("The bot is not currently in a channel.\n$PLAY_MESSAGE").queue()
            return
        }

        if (context.member.voiceState.channel != botChannel) {
            context.send().error("You're not in the same channel as the bot.").queue()
            return
        }

        if (manager.scheduler.queue.isEmpty()) {
            context.send().error("The queue is empty.\n$PLAY_MESSAGE").queue()
            return
        }

        manager.scheduler.shuffle()

        context.send().embed("Shuffle Queue") {
            desc { "Player has been shuffled" }
        }.action().queue()
    }
}
