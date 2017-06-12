package xyz.gnarbot.gnar

import gnu.trove.map.hash.TLongObjectHashMap
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import xyz.gnarbot.gnar.guilds.GuildData
import xyz.gnarbot.gnar.listeners.ShardListener

/**
 * Individual shard instances of [JDA] of the bot that contains all the [GuildData] for each guild.
 */
class Shard(val id: Int, val jda: JDA, val bot: Bot) : JDA by jda {
    /** @return the amount of successful requests on this command handler. */
    @JvmField var requests = 0

    val guildData = TLongObjectHashMap<GuildData>()

    init {
        jda.addEventListener(ShardListener(this, bot))
    }

    fun getGuildData(id: Long) : GuildData {
        val value = guildData[id]
        return if (value == null) {
            val answer = GuildData(id, this, bot)
            guildData.put(id, answer)
            answer
        } else {
            value
        }
    }

    /**
     * Lazily get a Host instance from a Guild instance.
     *
     * @param guild JDA Guild.
     *
     * @return Host instance of Guild.
     *
     * @see GuildData
     */
    fun getGuildData(guild: Guild) = getGuildData(guild.idLong)

    /**
     * @return The string representation of the shard.
     */
    override fun toString() = "Shard(id=$id, guilds=${jda.guilds.size})"

    /**
     * Shuts down the shard.
     */
    override fun shutdown() {
        jda.shutdown(false)
        clearData(true)
    }

    fun clearData(interrupt: Boolean) {
        val iterator = guildData.iterator()
        while (iterator.hasNext()) {
            iterator.advance()
            if(iterator.value().reset(interrupt)) {
                iterator.remove()
            }
        }
    }
}
