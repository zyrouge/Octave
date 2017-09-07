package xyz.gnarbot.gnar.commands.dispatcher.predicates

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.hasAnyRoleNamed
import java.util.function.BiPredicate

class PermissionPredicate : BiPredicate<CommandExecutor, Context> {
    override fun test(cmd: CommandExecutor, context: Context): Boolean {
        if (context.member.hasPermission(Permission.ADMINISTRATOR)
                || !(cmd.info.permissions.isNotEmpty() || cmd.info.roleRequirement.isNotEmpty())) return true

        if (context.member.hasAnyRoleNamed(cmd.info.roleRequirement)
                && cmd.info.scope.checkPermission(context, *cmd.info.permissions)) {
            return true
        }

        context.send().error(buildString {
            append("This command requires ")

            val permissionNotEmpty = cmd.info.permissions.isNotEmpty()

            if (cmd.info.roleRequirement.isNotEmpty()) {
                append("a role named `")
                append(cmd.info.roleRequirement)
                append('`')

                if (permissionNotEmpty) {
                    append(" and ")
                }
            }

            if (permissionNotEmpty) {
                append("the permissions `")
                append(cmd.info.permissions.map(Permission::getName))
                append("` in ")

                when (cmd.info.scope) {
                    Scope.GUILD -> {
                        append("the guild `")
                        append(context.guild.name)
                    }
                    Scope.TEXT -> {
                        append("the text channel `")
                        append(context.textChannel.name)
                    }
                    Scope.VOICE -> {
                        append("the voice channel `")
                        append(context.voiceChannel.name)
                    }
                }
            }

            append("`.")
        }).queue()
        return false
    }
}