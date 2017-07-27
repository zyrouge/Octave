package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 43,
        aliases = arrayOf("donate"),
        description = "Show the donation info."
)
class DonateCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        context.send().embed("Donations") {
            desc {
                buildString {
                    append("Want to donate to support Gnar?\n")
                    append("**[PayPal](https://gnarbot.xyz/donate)**\n")
                    append("**[Patreon](https://www.patreon.com/gnarbot)**\n")
                }
            }
        }.action().queue()
    }
}
