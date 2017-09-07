package xyz.gnarbot.gnar.commands.dispatcher.predicates

import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptionsOverride
import xyz.gnarbot.gnar.utils.Context
import java.util.function.BiPredicate

class SettingsPredicate : BiPredicate<CommandExecutor, Context> {
    override fun test(cmd: CommandExecutor, context: Context): Boolean {
        if (!cmd.info.toggleable) {
            return true
        }

        val options = CommandOptionsOverride(context.data.command.options[cmd.info.id], context.data.command.categoryOptions[cmd.info.category.ordinal])

        if (!options.isEnabled) {
            context.send().error("This ${type(options.inheritToggle())} is disabled.").queue()
            return false
        }

        return when {
            options.disabledUsers.contains(context.user.id) -> {
                context.send().error("You are not allowed to use this ${type(options.inheritUsers())}.").queue()
                false
            }
            options.disabledRoles.containsAll(context.member.roles.map(Role::getId)) -> {
                context.send().error("Your roles are not allowed to use this ${type(options.inheritRoles())}.").queue()
                false
            }
            options.disabledChannels.contains(context.textChannel.id) -> {
                context.send().error("You can not use this ${type(options.inheritChannels())} in this channel.").queue()
                false
            }
            else -> true
        }
    }

    private fun type(boolean: Boolean) = if (boolean) "category" else "command"
}
