package xyz.gnarbot.gnar

import net.dv8tion.jda.core.JDA
import xyz.gnarbot.gnar.guilds.GuildData

/**
 * Individual shard instances of [JDA] of the bot that contains all the [GuildData] for each guild.
 */
class Shard(val id: Int, val jda: JDA) : JDA by jda {
    /** @return the amount of successful requests on this command handler. */
    @JvmField var requests = 0

    /**
     * @return The string representation of the shard.
     */
    override fun toString() = "Shard(id=$id, guilds=${jda.guilds.size})"

    /**
     * Shuts down the shard.
     */
    override fun shutdown() {
        jda.shutdown(false)
    }
}
