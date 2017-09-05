package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.entities.ISnowflake
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions
import xyz.gnarbot.gnar.utils.Context

enum class ManageScope {
    USER {
        override fun all(context: Context): List<String> {
            return context.guild.members.map { it.user.id }
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledUsers
        }
    },
    ROLE {
        override fun all(context: Context): List<String> {
            return context.guild.roles.map(ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledRoles
        }
    },
    CHANNEL {
        override fun all(context: Context): List<String> {
            return context.guild.textChannels.map(ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledChannels
        }
    };

    abstract fun all(context: Context): List<String>
    abstract fun transform(map: CommandOptions): MutableSet<String>
}