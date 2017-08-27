package xyz.gnarbot.gnar.commands.executors.admin

import net.dv8tion.jda.core.entities.Icon
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import java.net.URL

@Command(
        id = 36,
        aliases = arrayOf("botAvatar"),
        category = Category.NONE,
        admin = true
)
class UpdateBotAvatarCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {

        } else {
            URL(args.joinToString(" ")).openStream().use {
                Bot.getShards()[0].jda.selfUser.manager.setAvatar(Icon.from(it)).queue()
            }
        }
    }
}
