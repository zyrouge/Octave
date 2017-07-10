package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Executor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("autorole"),
        usage = "(set|unset) [@role]",
        description = "Set auto-roles that are assigned to users on joining.",
        category = Category.MODERATION,
        permissions = arrayOf(Permission.MANAGE_ROLES)
)
class AutoRoleCommand : CommandTemplate() {
    @Executor(0, description = "Set the auto-role.")
    fun set(context: Context, role: Role) {
        if (!context.guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
            context.send().error("The bot needs the ${Permission.MANAGE_ROLES.getName()} permission.").queue()
            return
        }

        if (role == context.guild.publicRole) {
            context.send().error("You can't grant the public role!").queue()
            return
        }

        if (!context.guild.selfMember.canInteract(role)) {
            context.send().error("That role is higher than my role! Fix by changing the role hierarchy.").queue()
            return
        }

        if (role.id == context.guildOptions.autoRole) {
            context.send().error("${role.asMention} is already set as the auto-role.").queue()
            return
        }


        context.guildOptions.autoRole = role.id
        context.guildOptions.save()

        context.send().embed("Auto-Role") {
            desc {
                "Users joining the guild will now be granted the role ${role.asMention}."
            }
        }.action().queue()
    }

    @Executor(1, description = "Unset the auto-role.")
    fun unset(context: Context) {
        if (context.guildOptions.autoRole == null) {
            context.send().error("This guild doesn't have an auto-role.").queue()
            return
        }

        context.guildOptions.autoRole = null
        context.guildOptions.save()

        context.send().embed("Auto-Role") {
            desc {
                "Unset autorole. Users joining the guild will not be granted any role."
            }
        }.action().queue()
    }

    override fun noMatches(context: Context, args: Array<String>) {
        noMatches(context, args, buildString {
            if (!context.guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                append("**WARNING:** Bot lacks the ${Permission.MANAGE_ROLES.getName()} permission.")
                return
            }

            val role = context.guildOptions.autoRole
            append("Current auto-role: ")
            if (role == null) {
                append("__None__")
            } else {
                context.guild.getRoleById(role)?.asMention?.let { append(it) }
            }
        })
    }
}