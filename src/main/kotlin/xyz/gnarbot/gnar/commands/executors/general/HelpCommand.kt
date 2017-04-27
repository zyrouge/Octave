package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.b
import net.dv8tion.jda.core.link
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("help", "guide"),
        usage = "[command]",
        description = "Display GN4R's list of commands.",
        disableable = false
)
class HelpCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val registry = context.bot.commandRegistry

        if (args.isNotEmpty()) {
            val target = if (args[0].startsWith('_')) args[0].substring(1) else args[0]

            val cmd = registry.getCommand(target)

            if (cmd == null) {
                context.send().error("There is no command named `$target`. :cry:").queue()
                return
            }

            context.send().embed("Command Information") {
                color = BotConfiguration.ACCENT_COLOR

                field("Aliases", true, cmd.info.aliases.joinToString(separator = ", ${BotConfiguration.PREFIX}", prefix = BotConfiguration.PREFIX))
                field("Usage", true, "${BotConfiguration.PREFIX}${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}")
                field(true)

                if (cmd.info.permissions.isNotEmpty())
                    field("Guild Permission", true, "${cmd.info.scope} ${cmd.info.permissions.map(Permission::getName)}")



                field("Description", false, cmd.info.description)

            }.rest().queue()

            return
        }

        val cmds = registry.entries

        context.message.author.openPrivateChannel().queue {
            context.send(it).embed("Documentation") {
                color = BotConfiguration.ACCENT_COLOR
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
                                    append("**[").append(BotConfiguration.PREFIX).append(it.info.aliases[0]).appendln("]()**")
                                }
                            }
                        }
                    }
                }

                field(true)
                field("Additional Information") {
                    buildString {
                        append("To view a command's description, do `").append(BotConfiguration.PREFIX).appendln("help [command]`.")
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

        context.send().info("Gnar's guide has been directly messaged to you.\n\nNeed more support? Reach us on our __**[official support server](https://discord.gg/NQRpmr2)**__.").queue()
    }
}