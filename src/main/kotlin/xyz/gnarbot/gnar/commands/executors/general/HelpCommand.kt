package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 44,
        aliases = arrayOf("help", "guide"),
        usage = "[command]",
        description = "Display the bot's list of commands."
)
class HelpCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        if (args.isNotEmpty()) {
            val target = args[0]

            val category = try {
                Category.valueOf(target.toUpperCase())
            } catch (e: IllegalArgumentException) {
                null
            }

            if (category != null) {
                context.send().embed("${category.title} Commands") {
                    val filtered = registry.entries.filter {
                        it.info.category == category
                    }

                    field("Commands") {
                        buildString {
                            filtered.forEach {
                                append('`')
                                append(it.info.aliases.first())
                                append("` • ")
                                append(it.info.description).append('\n')
                            }
                        }
                    }
                }.action().queue()

                return
            }

            val cmd = registry.getCommand(target)
            if (cmd != null) {
                context.send().embed("Command Information") {
                    field("Aliases") { cmd.info.aliases.joinToString(separator = ", ${Bot.CONFIG.prefix}", prefix = Bot.CONFIG.prefix) }
                    field("Usage") { "_${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}" }
                    if (cmd.info.donor) {
                        field("Donator") { "This command is exclusive to donators' guilds. Donate to our Patreon or PayPal to gain access to them." }
                    }

                    if (cmd.info.permissions.isNotEmpty()) {
                        field("Required Permissions") { "${cmd.info.scope} ${cmd.info.permissions.map(Permission::getName)}" }
                    }

                    field("Description") { cmd.info.description }
                }.action().queue()
                return
            }

            context.send().error("There is no command or category named `$target`. :cry:").queue()
            return
        }

        val commands = registry.entries

        context.send().embed("Guides") {
            desc {
                buildString {
                    append("The prefix of the bot on this server is `").append(context.data.command.prefix ?: Bot.CONFIG.prefix).append("`.\n")
                    append("Donations: **[Patreon](https://gnarbot.xyz/donate)**\n")
                }
            }

            for (category in Category.values()) {
                if (!category.show) continue

                val filtered = commands.filter { it.info.category == category }
                if (filtered.isEmpty()) continue

                field("${category.title} — ${filtered.size}\n") {
                    filtered.joinToString("`   `", "`", "`") { it.info.aliases.first() }
                }
            }

            footer { "For more information try _help (command) or _help (category), ex: _help ttb or _help Music" }
        }.action().queue()
    }
}