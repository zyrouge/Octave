package xyz.gnarbot.gnar.commands.executors.general

import com.google.common.collect.Lists
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.*

@Command(
        aliases = arrayOf("help", "guide"),
        usage = "[command]",
        description = "Display GN4R's list of commands.",
        toggleable = false
)
class HelpCommand : CommandExecutor() {
    var lazyEmbed: MessageEmbed? = null

    override fun execute(context: Context, args: Array<String>) {
        val registry = Bot.getCommandRegistry()

        if (args.isNotEmpty()) {
            val target = if (args[0].startsWith('_')) args[0].substring(1) else args[0]

            val cmd = registry.getCommand(target)

            if (cmd == null) {
                context.send().error("There is no command named `$target`. :cry:").queue()
                return
            }

            context.send().embed("Command Information") {
                field("Aliases", true) { cmd.info.aliases.joinToString(separator = ", ${Bot.CONFIG.prefix}", prefix = Bot.CONFIG.prefix) }
                field("Usage", true) { "${Bot.CONFIG.prefix}${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}" }
                if (cmd.info.donor) {
                    field("Donator", true) { "This command is exclusive to donators' guilds. Donate to our Patreon or PayPal to gain access to them." }
                } else {
                    field(true)
                }

                if (cmd.info.permissions.isNotEmpty()) {
                    field("Guild Permission", true) { "${cmd.info.scope} ${cmd.info.permissions.map(Permission::getName)}" }
                }

                field("Description") { cmd.info.description }
            }.action().queue()

            return
        }

        val cmds = registry.entries

        context.message.author.openPrivateChannel().queue {
            if (lazyEmbed == null) {
                lazyEmbed = embed("Documentation") {
                    setColor(Bot.CONFIG.accentColor)
                    setDescription("This is all of Gnar's currently registered commands.")

                    for (category in Category.values()) {
                        if (!category.show) continue

                        val filtered = cmds.filter {
                            it.info.category == category
                        }
                        if (filtered.isEmpty()) continue

                        val pages = Lists.partition(filtered, filtered.size / 3 + (if (filtered.size % 3 == 0) 0 else 1))

                        field(true)
                        field("${category.title} — ${filtered.size}\n") { category.description }

                        for (page in pages) {
                            field("", true) {
                                buildString {
                                    page.forEach {
                                        append("[").append(Bot.CONFIG.prefix).append(it.info.aliases[0]).append("]()")

                                        if (it.info.donor) {
                                            append(" 🌟").ln()
                                        } else {
                                            ln()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    field(true)
                    field("Additional Information") {
                        buildString {
                            append("To view a command's description, do `").append(Bot.CONFIG.prefix).append("help [command]`.").ln()
                            append("🌟 are donator commands. Donate to our Patreon or PayPal to gain access to them.").ln()
                        }
                    }

                    field("News") {
                        buildString {
                            append("First donator command?! `_volume`")
                        }
                    }

                    field("Contact", true) {
                        buildString {
                            append(b("Website" link "http://gnarbot.xyz")).ln()
                            append(b("Discord Server" link "http://discord.gg/NQRpmr2")).ln()
                        }
                    }

                    field("Donations", true) {
                        buildString {
                            append(b("Patreon" link "https://www.patreon.com/gnarbot")).ln()
                            append(b("PayPal" link "https://gnarbot.xyz/donate")).ln()
                        }
                    }
                }.build()
            }

            it.sendMessage(lazyEmbed).queue()
        }

        context.send().info("Gnar's guide has been directly messaged to you.\n\n" +
                "Need more support? Reach us on our __**[official support server](https://discord.gg/NQRpmr2)**__.").queue()
    }
}