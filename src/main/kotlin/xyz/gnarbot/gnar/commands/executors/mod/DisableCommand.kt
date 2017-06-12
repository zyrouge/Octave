package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("disable"),
        usage = "[commands...]",
        description = "Disable commands.",
        toggleable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class DisableCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Disabled Commands") {
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

        val disabled = args
                .map {
                    context.guildData.commandHandler.disableCommand(it).apply {
                        if (this == null) fails += it
                    }
                }
                .filterNotNull()
                .map { it.info.aliases[0] }

        context.send().embed("Disabling Commands") {

            if (disabled.isNotEmpty()) {
                field("Success") {
                    buildString {
                        append("Disabled ${disabled.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")} command(s) on this server.").ln()
                    }
                }
            }
            if (fails.isNotEmpty()) {
                field("Failure") {
                    buildString {
                        append("Unable to disable ${fails.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")}.").ln()
                        append("Either that they are not commands or could not be disabled.")
                    }
                }
            }
        }.action().queue()
    }
}