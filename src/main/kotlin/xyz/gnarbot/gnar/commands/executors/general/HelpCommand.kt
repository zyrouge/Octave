package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.b
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.link
import xyz.gnarbot.gnar.Constants
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor

@Command(
        aliases = arrayOf("help", "guide"),
        usage = "[command]",
        description = "Display GN4R's list of commands.",
        disableable = false
)
class HelpCommand : CommandExecutor() {

    override fun execute(message: Message, args: Array<String>) {
        val registry = bot.commandRegistry

        if (args.isNotEmpty()) {
            val target = if (args[0].startsWith('_')) args[0].substring(1) else args[0]

            val entry = registry.getEntry(target)

            if (entry == null) {
                message.respond().error("There is no command named `$target`. :cry:").queue()
                return
            }

            message.respond().embed("Command Information") {
                color = Constants.COLOR

                field("Aliases", true, entry.info.aliases.joinToString(separator = ", ${Constants.PREFIX}", prefix = Constants.PREFIX))
                field("Usage", true, "${Constants.PREFIX}${entry.info.aliases[0].toLowerCase()} ${entry.info.usage}")
                field(true)

                if (entry.info.permissions.isNotEmpty())
                    field("Guild Permission", true, "${entry.info.scope} ${entry.info.permissions.map(Permission::getName)}")



                field("Description", false, entry.info.description)

            }.rest().queue()

            return
        }

        val cmds = registry.entries

        message.author.openPrivateChannel().queue {
            it.send().embed("Documentation") {
                color = Constants.COLOR
                description = "This is all of Gnar's currently registered commands."

                for (category in Category.values()) {
                    if (!category.show) continue

                    val filtered = cmds.filter {
                        it.info.category == category
                    }
                    if (filtered.isEmpty()) continue

                    val pages = Lists.partition(filtered,
                            filtered.size / 3 + (if (filtered.size % 3 == 0) 0 else 1))

                    field(true)
                    field("${category.title} — ${filtered.size}\n", false, category.description)

                    for (page in pages) {
                        field("", true) {
                            buildString {
                                page.forEach {
                                    append("**[").append(Constants.PREFIX).append(it.info.aliases[0]).appendln("]()**")
                                }
                            }
                        }
                    }
                }

                field(true)
                field("Additional Information") {
                    buildString {
                        append("To view a command's description, do `").append(Constants.PREFIX).appendln("help [command]`.")
                        append("__The commands that requires a named role must be created by you and assigned to a member in your guild.__")
                    }
                }

                field("News") {
                    buildString {
                        appendln("• **Incomplete**: disable commands with `_enable | _disable | _listdisabled`.")
                        appendln("• No more role names! **Commands are now linked with Discord permissions.**")
                        appendln("  - DJ commands for instance, requires you to be in a voice channel and have the `Manage Channel` permission of the channel.")
                        appendln("  - Ban and kick commands requires the `Ban Member` and `Kick Member` permission respectively.")
                        appendln("• `_nowplaying` links to the original songs.")
                        appendln("• Optimizations, basically rewritten the bot's systems.")
                    }
                }

                field("Contact", true) {
                    buildString {
                        appendln(b("Website" link "http://gnarbot.xyz"))
                        appendln(b("Discord Server" link "http://discord.gg/NQRpmr2"))
                    }
                }

                field("Donations", true) {
                    buildString {
                        appendln(b("PayPal" link "https://gnarbot.xyz/donate"))
                        appendln(b("Patreon" link "https://www.patreon.com/gnarbot"))
                    }
                }
            }.rest().queue()
        }

        message.respond().info("Gnar's guide has been directly messaged to you.\n\nNeed more support? Reach us on our __**[official support server](https://discord.gg/NQRpmr2)**__.").queue()
    }
}