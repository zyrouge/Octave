package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 58,
        aliases = arrayOf("resetall"),
        description = "Reset all music settings.",
        category = Category.SETTINGS,
        toggleable = false,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ResetSettingsCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.data.reset()
        context.data.save()

        context.send().embed("Settings") {
            desc { "The guild options for this guild have been reset." }
        }.action().queue()
    }
}