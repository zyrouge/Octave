package xyz.gnarbot.gnar.commands.executors.music.dj

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.executors.music.MusicCommandExecutor
import xyz.gnarbot.gnar.commands.template.Parsers
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 77,
        aliases = arrayOf("move"),
        description = "Move the bot to another channel.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        roleRequirement = "DJ"
)
class MoveCommand : MusicCommandExecutor(false, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val targetChannel = if (args.isEmpty()) {
            context.voiceChannel
        } else {
            Parsers.VOICE_CHANNEL.parse(context, args.joinToString(" "))
        }

        if (targetChannel == null) {
            context.send().error("That's not a valid music channel.").queue()
            return
        }

        if (targetChannel == context.selfMember.voiceState.channel) {
            context.send().error("That's the same channel.").queue()
            return
        }

        if (context.data.music.channels.isNotEmpty()) {
            if (targetChannel.id !in context.data.music.channels) {
                context.send().error(
                        "Can not join `${targetChannel.name}`, it isn't one of the designated music channels."
                ).queue()
                return
            }
        }

        context.guild.audioManager.openAudioConnection(targetChannel)
        // assume magic from VoiceListener.kt
    }
}
