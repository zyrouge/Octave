package xyz.gnarbot.gnar.commands.executors.general

import net.dv8tion.jda.core.b
import net.dv8tion.jda.core.link
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("donate"),
        description = "Show the getBot's uptime."
)
class DonateCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.send().embed("Donations") {
            color = BotConfiguration.ACCENT_COLOR
            description {
                buildString {
                    appendln("Want to donate to support Gnar?")
                    appendln(b("PayPal" link "https://gnarbot.xyz/donate"))
                    appendln(b("Patreon" link "https://www.patreon.com/gnarbot"))
                }
            }
        }.rest().queue()
    }
}
