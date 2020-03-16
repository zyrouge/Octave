package xyz.gnarbot.gnar.commands.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["djrole"],
        usage = "(set|reset) [role]",
        description = "Set a DJ role name to be used as the DJ role"
)
@BotInfo(
        id = 152,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_ROLES]
)
class DJRoleCommand : CommandTemplate() {
    @Description("Set a DJ role name to be used as the DJ role")
    fun set(context: Context, role: Role?) {
        if(role == null) {
            context.send().error("The role doesn't exist.").queue()
            return
        }

        context.data.command.djRole = role.id
        context.data.save()

        context.send().info("Successfully set the DJ Role to ${role.name} (${role.id}).").queue()
    }

    @Description("Reset the DJ role name")
    fun reset(context: Context) {
        context.data.command.djRole = null
        context.data.save()

        context.send().info("Successfully reset DJ role name.").queue()
    }
}