package xyz.gnarbot.gnar.guilds

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.commands.CommandDispatcher
import xyz.gnarbot.gnar.commands.CommandHandler
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context

class GuildData(val id: Long, val shard: Shard, val bot: Bot) : CommandHandler {
    val guild : Guild get() = shard.getGuildById(id)

    val commandHandler = CommandDispatcher(this, bot)

    private var musicManager_delegate: MusicManager? = null
        get() {
            if (field == null) {
                field = MusicManager(this, bot.playerManager)
                field!!.player.volume = 35
            }
            return field
        }

    var musicManager: MusicManager
        get() = musicManager_delegate!!
        set(value) {
            musicManager_delegate = value
        }

    fun resetMusicManager() {
        musicManager.scheduler.queue.clear()
        musicManager.player.destroy()
        guild.audioManager.closeAudioConnection()
        guild.audioManager.sendingHandler = null
        musicManager_delegate = null
    }

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
        musicManager_delegate?.let {
            if (!interrupt && it.player.playingTrack != null) {
                return false
            }
        }

        resetMusicManager()
        return true
    }

    override fun handleCommand(context: Context) {
        if(commandHandler.callCommand(context)) {
            bot.requests++
        }
    }
}
