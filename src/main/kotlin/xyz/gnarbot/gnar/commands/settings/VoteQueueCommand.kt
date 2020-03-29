package xyz.gnarbot.gnar.commands.settings

import net.dv8tion.jda.api.Permission
import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.annotations.Description

@Command(
        aliases = ["votequeue"],
        usage = "(enable|disable)",
        description = "Enables or disable voting to queue songs."
)
@BotInfo(
        id = 156,
        category = Category.SETTINGS,
        permissions = [Permission.MANAGE_SERVER]
)
class VoteQueueCommand : CommandTemplate() {
    @Description("Enables voting to queue songs.")
    fun enable(context: Context) {
        context.data.music.isVotePlay = true
        context.data.save()

        context.send().info("Enabled vote play.").queue()
    }

    @Description("Disables voting to queue songs.")
    fun disable(context: Context) {
        context.data.music.isVotePlay = false
        context.data.save()

        context.send().info("Successfully disabled vote play.").queue()
    }
}