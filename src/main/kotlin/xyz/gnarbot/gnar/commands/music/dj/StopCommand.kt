package xyz.gnarbot.gnar.commands.music.dj

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.commands.music.MusicCommandExecutor
import xyz.gnarbot.gnar.music.MusicManager

@Command(
        aliases = ["stop", "leave", "end"],
        description = "Stop and clear the music player."
)
@BotInfo(
        id = 61,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        roleRequirement = "DJ"
)
class StopCommand : MusicCommandExecutor(false, false, false) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        manager.discordFMTrack = null
        context.guild.audioManager.closeAudioConnection()
        context.bot.players.destroy(context.guild.idLong)

        context.send().info("Playback has been completely stopped and the queue has been cleared.").queue()
    }
}
