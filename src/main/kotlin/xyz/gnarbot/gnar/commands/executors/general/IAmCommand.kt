package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 45,
        aliases = ["iam"],
        usage = "(a|not) (@role)",
        description = "Assign yourself a self-role."
)
class IAmCommand : CommandTemplate() {
    @Description("Assign yourself a self-role.")
    fun a(context: Context, role: Role) {
        if (role.id !in context.data.roles.selfRoles) {
            return context.send().error("${role.asMention} is not a self-assignable role.").queue()
        }

        if (role in context.member.roles) {
            return context.send().error("You're already a ${role.asMention}.").queue()
        }

        context.guild.controller.addSingleRoleToMember(context.member, role).queue()

        context.send().embed("Self-Roles") {
            desc {
                "You're now a ${role.asMention}."
            }
        }.action().queue()

    }

    @Description("Remove a self-role from yourself.")
    fun not(context: Context, role: Role) {
        if (role.id !in context.data.roles.selfRoles) {
            return context.send().error("${role.asMention} is not a self-assignable role.").queue()
        }

        if (role !in context.member.roles) {
            return context.send().error("You're not a ${role.asMention}.").queue()
        }

        context.guild.controller.removeSingleRoleFromMember(context.member, role).queue()

        context.send().embed("Self-Roles") {
            desc {
                "You're no longer a ${role.asMention}."
            }
        }.action().queue()
    }

    @Description("List self-assignable roles.")
    fun list(context: Context) {
        context.send().embed("Self-Roles") {
            desc {
                if (context.data.roles.selfRoles.isEmpty()) {
                    "This guild doesn't have any self-assignable roles."
                } else {
                    buildString {
                        context.data.roles.selfRoles.mapNotNull {
                            context.guild.getRoleById(it)
                        }.forEach {
                            append("• ").append(it.asMention).append('\n')
                        }
                    }
                }
            }
        }.action().queue()
    }
}