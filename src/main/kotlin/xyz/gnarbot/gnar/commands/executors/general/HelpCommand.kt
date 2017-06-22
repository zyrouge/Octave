package xyz.gnarbot.gnar.commands.executors.general

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
                field("Aliases") { cmd.info.aliases.joinToString(separator = ", ${Bot.CONFIG.prefix}", prefix = Bot.CONFIG.prefix) }
                field("Usage") { "${Bot.CONFIG.prefix}${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}" }
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

        val cmds = registry.entries

        if (lazyEmbed == null) {
            lazyEmbed = embed("Documentation") {
                setColor(Bot.CONFIG.accentColor)
                setDescription("The prefix of the bot on this server is `" + Bot.CONFIG.prefix + "`.")

                for (category in Category.values()) {
                    if (!category.show) continue

                    val filtered = cmds.filter {
                        it.info.category == category
                    }
                    if (filtered.isEmpty()) continue

                    field("${category.title} — ${filtered.size}\n") {
                        filtered.joinToString("`   `", "`", "`") { it.info.aliases.first() }
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

        context.channel.sendMessage(lazyEmbed).queue()

//        context.send().info("Gnar's guide has been directly messaged to you.\n"
//                + "Need more support? Reach us on our __**[official support server](https://discord.gg/NQRpmr2)**__.\n"
//                + "Please consider donating to our __**[Patreon](https://www.patreon.com/gnarbot)**__.").queue()
    }
}