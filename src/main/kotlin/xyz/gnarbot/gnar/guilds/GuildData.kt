package xyz.gnarbot.gnar.guilds

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.commands.CommandDispatcher
import xyz.gnarbot.gnar.commands.CommandHandler
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

class GuildData(val id: Long, val shard: Shard) : CommandHandler {
    val guild : Guild get() = shard.getGuildById(id)

    val commandHandler = CommandDispatcher()

    val musicManager: MusicManager = MusicManager(this)
        get() {
            return field.apply { if (!isSetup) setup() }
        }

    fun isPremium(): Boolean = Bot.CONFIG.donors.contains(id)

    fun getMemberByName(name: String, searchNickname: Boolean = false): Member? {
        for (member in guild.getMembersByName(name, true)) {
            return member
        }
        if (searchNickname) {
            for (member in guild.getMembersByNickname(name, true)) {
                return member
            }
        }
        return null
    }

    fun reset(interrupt: Boolean) : Boolean {
        if (!interrupt && musicManager.player.playingTrack != null) {
            return false
        }
        musicManager.reset()
        return true
    }

    override fun handleCommand(context: Context) {
        if (!guild.selfMember.hasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            context.send().text("Grant the bot `Embed Links` permission to see messages.")
                    .queue(Utils.deleteMessage(15))
            return
        }

        launch(CommonPool) {
            if (commandHandler.callCommand(context)) {
                shard.requests++
            }
        }
    }
}
