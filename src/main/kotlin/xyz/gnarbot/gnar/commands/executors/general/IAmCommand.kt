package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.commands.managed.ManagedCommand
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("iam"),
        usage = "(a|not) (@role)",
        description = "Assign yourself a self-role."
)
class IAmCommand : ManagedCommand() {
    @Executor(0, description = "Assign yourself a self-role.")
    fun a(context: Context, role: Role) {
        if (role.id !in context.guildOptions.selfRoles) {
            return context.send().error("${role.asMention} is not a self-assignable role.").queue()
        }

        if (role in context.member.roles) {
            return context.send().error("You're already a ${role.asMention}.").queue()
        }

        context.guild.controller.addRolesToMember(context.member, role).queue()

        context.send().embed("Self-Roles") {
            description {
                "You're now a ${role.asMention}."
            }
        }.action().queue()

    }

    @Executor(1, description = "Remove a self-role from yourself.")
    fun not(context: Context, role: Role) {
        if (role.id !in context.guildOptions.selfRoles) {
            return context.send().error("${role.asMention} is not a self-assignable role.").queue()
        }

        if (role !in context.member.roles) {
            return context.send().error("You're not a ${role.asMention}.").queue()
        }

        context.guild.controller.removeRolesFromMember(context.member, role).queue()

        context.send().embed("Self-Roles") {
            description {
                "You're no longer a ${role.asMention}."
            }
        }.action().queue()
    }

    @Executor(2, description = "List self-assignable roles.")
    fun list(context: Context) {
        context.send().embed("Self-Roles") {
            description {
                if (context.guildOptions.selfRoles.isEmpty()) {
                    "This guild doesn't have any self-assignable roles."
                } else {
                    buildString {
                        context.guildOptions.selfRoles.map {
                            context.guild.getRoleById(it)
                        }.filterNotNull().forEach {
                            append("• ").append(it.asMention).ln()
                        }
                    }
                }
            }
        }.action().queue()
    }
}