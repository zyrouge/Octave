package xyz.gnarbot.gnar.commands.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager

@Command(
    aliases = ["speed", "speeeeeeeed"],
    description = "Changes the speed of the music"
)
@BotInfo(
    id = 666,
    category = Category.MUSIC,
    scope = Scope.VOICE,
    djLock = true
)

class SpeedCommand : MusicCommandExecutor(true, true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        val speed = args.firstOrNull()?.toDoubleOrNull()?.coerceIn(0.0, 3.0)
            ?: return context.send().info("You need to specify a number between 0.0 and 3.0").queue()

        manager.setSpeed(speed)
    }
}