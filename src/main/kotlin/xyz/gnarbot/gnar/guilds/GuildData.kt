package xyz.gnarbot.gnar.guilds

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.db.Database
import xyz.gnarbot.gnar.db.ManagedObject
import xyz.gnarbot.gnar.music.MusicManager

data class GuildData(val id: Long): ManagedObject {
    val shard: Shard = Bot.getShards()[((id shr 22) % Bot.KEYS.shards).toInt()]

    val guild: Guild get() = shard.jda.getGuildById(id)

    val options: GuildOptions = Bot.DATABASE.getGuildOptions(id.toString())?.also {
        Database.LOG.info("Loaded $it from database.")
        it.disabledCommands.removeIf { !Bot.getCommandRegistry().commandMap.containsKey(it) }
    } ?: GuildOptions(id.toString())

    val musicManager: MusicManager = MusicManager(this)
        get() {
            return field.apply { if (!isSetup) setup() }
        }

    fun isPremium(): Boolean = options.isPremium()

    fun getMemberByName(name: String, searchNickname: Boolean = false): Member? {
        for (member in guild.getMembersByName(name, true)) {
            return member
        }
        if (searchNickname) {
            guild.getMembersByNickname(name, true).firstOrNull()
        }
        return null
    }

    override fun save() {
        options.save()
    }

    override fun delete() {
        options.delete()
    }
}
