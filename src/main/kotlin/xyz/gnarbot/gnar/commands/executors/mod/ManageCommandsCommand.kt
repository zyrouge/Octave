package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

//TODO incomplete
@Command(
        aliases = arrayOf("cmd", "command", "cmds", "commands"),
        usage = "[commands...]",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class ManageCommandsCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        if (args.isEmpty()) {
            context.send().embed("Command Management") {
                description {
                    buildString {
                        append("`enable` • Enable a command.").ln()
                        append("`disable` • Disable a command.").ln()
                        append("`list` • List disabled commands.").ln()
                    }
                }
            }.action().queue()
            return
        }

        when (args[0]) {
            "enable" -> {
                if (args.size != 2) {
                    context.send().error("Please input a command argument `ie. _cmd enable roll`.").queue()
                    return
                }
                val aliases = lookupCommand(args[1])?.info?.aliases

                if (aliases == null || aliases.isEmpty()) {
                    context.send().error("`${args[1]}` is not a valid command.").queue()
                    return
                }

                context.guildData.options.disabledCommands.addAll(aliases)
            }
            "disable" -> {
                if (args.size != 2) {
                    context.send().error("Please input a command argument `ie. _cmd disable roll`.").queue()
                    return
                }
                val aliases = lookupCommand(args[1])?.info?.aliases

                if (aliases == null || aliases.isEmpty()) {
                    context.send().error("`${args[1]}` is not a valid command.").queue()
                    return
                }

                context.guildData.options.disabledCommands.removeAll(aliases)
            }
        }

//        if (args.isEmpty()) {
//            context.send().embed("Disabled Commands") {
//                description {
//                    if (context.guildData.commandHandler.disabled.isEmpty()) {
//                        "There isn't any command disabled on this server."
//                    }
//                    else buildString {
//                        context.guildData.commandHandler.disabled.forEach {
//                            append("• ")
//                            appendln(it.info.aliases.joinToString())
//                        }
//                    }
//                }
//            }.action().queue()
//            return
//        }
//
//        val fails = mutableListOf<String>()
//
//        val enabled = args
//                .map {
//                    context.guildData.commandHandler.enableCommand(it).apply {
//                        if (this == null) fails += it
//                    }
//                }
//                .filterNotNull()
//                .map { it.info.aliases[0] }
//
//        context.send().embed("Enabling Commands") {
//
//            if (enabled.isNotEmpty()) {
//                field("Success") {
//                    buildString {
//                        append("Enabled ${enabled.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")} command(s) on this server.").ln()
//                    }
//                }
//            }
//            if (fails.isNotEmpty()) {
//                field("Failure") {
//                    buildString {
//                        append("Unable to enable ${fails.joinToString(prefix = "`_", separator = "`, `_", postfix = "`")}.").ln()
//                        append("Either that they are not commands or could not be enabled.")
//                    }
//                }
//            }
//        }.action().queue()
    }

    private fun lookupCommand(label: String): CommandExecutor? {
        return Bot.getCommandRegistry().getCommand(label)
    }
}