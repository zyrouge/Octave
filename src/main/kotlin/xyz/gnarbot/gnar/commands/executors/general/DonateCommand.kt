package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.b
import xyz.gnarbot.gnar.utils.link
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("donate"),
        description = "Show the getBot's uptime."
)
class DonateCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        context.send().embed("Donations") {
            description {
                buildString {
                    append("Want to donate to support Gnar?").ln()
                    append(b("PayPal" link "https://gnarbot.xyz/donate")).ln()
                    append(b("Patreon" link "https://www.patreon.com/gnarbot")).ln()
                }
            }
        }.action().queue()
    }
}
