package xyz.gnarbot.gnar.guilds

import net.dv8tion.jda.core.entities.Guild
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.Shard
import xyz.gnarbot.gnar.music.MusicManager

data class GuildData(val id: Long){
    val shard: Shard = Bot.getShards()[((id shr 22) % Bot.KEYS.shards).toInt()]

    val guild: Guild get() = shard.jda.getGuildById(id)

    val musicManager: MusicManager = MusicManager(this)
        get() {
            return field.apply { if (!isSetup) setup() }
        }
}
