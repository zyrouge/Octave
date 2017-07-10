package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Executor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("cmd", "command", "cmds", "commands"),
        usage = "(enable|disable|list) [commands...]",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ManageCommandsCommand : CommandTemplate() {
    @Executor(0, description = "Enable commands.")
    fun enable(context: Context, cmd: String) {
        val aliases = lookupCommand(cmd)?.info?.aliases

        if (aliases == null || aliases.isEmpty()) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        }

        val list = aliases.filter(context.guildOptions.disabledCommands::contains)

        if (list.isEmpty()) {
            context.send().error("`$cmd` is not disabled.").queue()
            return
        }

        context.guildOptions.disabledCommands.removeAll(list)
        context.guildOptions.save()

        context.send().info("Enabling ${list.joinToString { "`$it`" }}.").queue()
    }

    @Executor(1, description = "Disable commands.")
    fun disable(context: Context, cmd: String) {
        val info = lookupCommand(cmd)?.info

        if (info == null) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        } else if (!info.toggleable) {
            context.send().error("`$cmd` can not be toggled.").queue()
            return
        }

        val aliases = info.aliases

        if (aliases.isEmpty()) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        }

        val list = aliases.filterNot(context.guildOptions.disabledCommands::contains)

        if (list.isEmpty()) {
            context.send().error("`$cmd` is already disabled.").queue()
            return
        }

        context.guildOptions.disabledCommands.addAll(list)
        context.guildOptions.save()

        context.send().info("Disabling ${list.joinToString { "`$it`" }}.").queue()
    }

    @Executor(2, description = "Clear all disabled commands.")
    fun clear(context: Context) {
        if (context.guildOptions.disabledCommands.isEmpty()) {
            context.send().error("This guild doesn't have any disabled commands.").queue()
            return
        }

        context.guildOptions.disabledCommands.clear()
        context.guildOptions.save()

        context.send().embed("Command Management") {
            desc {
                "Cleared the list of self-assignable roles."
            }
        }.action().queue()
    }

    @Executor(3, description = "List all disabled commands.")
    fun list(context: Context) {
        context.send().embed("Disabled Commands") {
            desc {
                if (context.guildOptions.disabledCommands.isEmpty()) {
                    "No commands disabled!? Hooray!"
                } else buildString {
                    context.guildOptions.disabledCommands.forEach {
                        append("• `").append(it).append("`\n")
                    }
                }
            }
        }.action().queue()
    }

    private fun lookupCommand(label: String): CommandExecutor? {
        return Bot.getCommandRegistry().getCommand(label)
    }
}