package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context

@Command(
        aliases = ["donate"],
        description = "Show the donation info."
)
@BotInfo(
        id = 43
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
