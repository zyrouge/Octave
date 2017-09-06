package xyz.gnarbot.gnar.commands.filters

import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions
import xyz.gnarbot.gnar.utils.Context
import java.util.*
import java.util.function.BiPredicate

class SettingsFilter : BiPredicate<CommandExecutor, Context> {
    override fun test(cmd: CommandExecutor, context: Context): Boolean {
        if (!cmd.info.toggleable) {
            return true
        }

        val type: String
        var options: CommandOptions? = context.data.command.options[cmd.info.id]
        if (options != null) {
            type = "command"
        } else {
            options = context.data.command.categoryOptions[cmd.info.category.ordinal]
            if (options != null) {
                type = "category"
            } else {
                return true
            }
        }

        if (options.disabledUsers.contains(context.user.id)) {
            context.send().error("You are not allowed to use this $type.").queue()
            return false
        } else if (!Collections.disjoint(options.disabledRoles, context.member.roles.map(Role::getId))) {
            context.send().error("Your role is not allowed to use this $type.").queue()
            return false
        } else if (options.disabledChannels.contains(context.textChannel.id)) {
            context.send().error("You can not use this $type in this channel.").queue()
            return false
        }
        return true
    }
}
