package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("enable"),
        usage = "[commands...]",
        description = "Enable commands.",
        disableable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class EnableCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Disabled Commands") {
                color = context.bot.config.accentColor
                description {
                    if (context.guildData.commandHandler.disabled.isEmpty()) {
                        "There isn't any command disabled on this server."
                    }
                    else buildString {
                        context.guildData.commandHandler.disabled.forEach {
                            append("• ")
                            appendln(it.info.aliases.joinToString())
                        }
                    }
                }
            }.action().queue()
            return
        }

        val fails = mutableListOf<String>()

        val enabled = args
                .map {
                    context.guildData.commandHandler.enableCommand(it).apply {
                        if (this == null) fails += it
                    }
                }
                .filterNotNull()
                .map { it.info.aliases[0] }

        context.send().embed("Enabling Commands") {
            color = context.bot.config.accentColor
            if (enabled.isNotEmpty()) {
                field("Success") {
                    buildString {
                        append("Enabled ${enabled.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")} command(s) on this server.").ln()
                    }
                }
            }
            if (fails.isNotEmpty()) {
                field("Failure") {
                    buildString {
                        append("Unable to enable ${fails.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")}.").ln()
                        append("Either that they are not commands or could not be enabled.")
                    }
                }
            }
        }.action().queue()
    }
}