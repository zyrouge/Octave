package xyz.gnarbot.gnar.commands.executors.music.dj

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.executors.music.PLAY_MESSAGE
import xyz.gnarbot.gnar.commands.template.Parser
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 77,
        aliases = arrayOf("move"),
        description = "Move the bot to a channel.",
        category = Category.MUSIC,
        scope = Scope.VOICE,
        permissions = arrayOf(Permission.VOICE_MOVE_OTHERS)
)
class MoveCommand : CommandExecutor() {
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

        val targetChannel = if (args.isEmpty()) {
            context.member.voiceState.channel
        } else {
            Parser.VOICE_CHANNEL.parse(context, args.joinToString(" "))
        }

        if (targetChannel == null) {
            context.send().error("That's not a valid music channel.").queue()
            return
        }

        if (targetChannel == botChannel) {
            context.send().error("That's the same channel.").queue()
            return
        }

        if (context.guildOptions.musicChannels.isNotEmpty()) {
            if (targetChannel.id !in context.guildOptions.musicChannels) {
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
