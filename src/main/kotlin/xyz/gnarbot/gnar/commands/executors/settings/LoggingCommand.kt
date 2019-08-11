package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["joinLog"],
        usage = "(set|unset) [#channel]",
        description = "Set logging channels for join messages"
)
@BotInfo(
        id = 52,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_ROLES]
)
class LoggingCommand : CommandTemplate() {
    @Description("Enable logging messages for a channel")
    fun set(context: Context, member: Member) {


    }
}