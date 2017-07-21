package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 58,
        aliases = arrayOf("resetall"),
        description = "Reset all music settings.",
        category = Category.MODERATION,
        toggleable = false,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ResetSettingsCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        Bot.getOptions().deleteGuild(context.guild)

        context.send().embed("Settings") {
            desc {
                "The guild options have been reset."
            }
        }.action().queue()
    }
}