package xyz.gnarbot.gnar.guilds

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.commands.CommandDispatcher
import xyz.gnarbot.gnar.commands.CommandHandler
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

class GuildData(val id: Long, val shard: Shard, val bot: Bot) : CommandHandler {
    val guild : Guild get() = shard.getGuildById(id)

    val commandHandler = CommandDispatcher(bot)

    val musicManager: MusicManager = MusicManager(this)
        get() {
            return field.also { if (!field.isSetup) field.setup() }
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
        if (!interrupt && musicManager.player.playingTrack != null) {
            return false
        }
        musicManager.reset()
        return true
    }

    override fun handleCommand(context: Context) {
        try {
            Thread {
                if (commandHandler.callCommand(context)) {
                    shard.requests++
                }
            }.start()
        } catch (e: PermissionException) {
            if (e.permission == Permission.MESSAGE_EMBED_LINKS) {
                context.send().text("Most of the bot's messages are sent as embeds.\nGrant the bot `Embed Links` permission to see messages.")
                        .queue(Utils.deleteMessage(15))
            }
        }
    }
}
