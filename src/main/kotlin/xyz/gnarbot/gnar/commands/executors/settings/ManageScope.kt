package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.entities.ISnowflake
import xyz.gnarbot.gnar.commands.template.Description
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions
import xyz.gnarbot.gnar.utils.Context

@Description("The scope of the command")
enum class ManageScope {
    USER {
        override fun all(context: Context): Set<String> {
            return context.guild.members.mapTo(HashSet()) { it.user.id }
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledUsers
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledUsers()
        }
    },
    ROLE {
        override fun all(context: Context): Set<String> {
            return context.guild.roles.mapTo(HashSet(), ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledRoles
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledRoles()
        }
    },
    CHANNEL {
        override fun all(context: Context): Set<String> {
            return context.guild.textChannels.mapTo(HashSet(), ISnowflake::getId)
        }
        override fun transform(map: CommandOptions): MutableSet<String> {
            return map.disabledChannels
        }
        override fun rawTransform(map: CommandOptions): MutableSet<String>? {
            return map.rawDisabledChannels()
        }
    };

    abstract fun all(context: Context): Set<String>
    abstract fun transform(map: CommandOptions): MutableSet<String>
    abstract fun rawTransform(map: CommandOptions): MutableSet<String>?
}