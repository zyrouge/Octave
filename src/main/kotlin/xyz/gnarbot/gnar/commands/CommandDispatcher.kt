package xyz.gnarbot.gnar.commands

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.filters.*
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

class CommandDispatcher{
    private val namePrefix = "${Bot.CONFIG.name.toLowerCase()} "

    private val filters = listOf(IgnoredFilter(), AdministratorFilter(), SettingsFilter(), DonatorFilter(), PermissionFilter(), VoiceFilter())

    private val permissionToSpeak = "The bot needs the `${Permission.MESSAGE_EMBED_LINKS.getName()}` permission in this channel to show messages."

    fun handle(context: Context) {
        val content = context.message.rawContent
        if (!content.startsWith(Bot.CONFIG.prefix)
                && !content.startsWith(namePrefix, true)) {
            val prefix = context.data.command.prefix
            if (prefix == null || !content.startsWith(prefix)) return
        }

        // Don't do anything if the bot can't even speak.
        if (!context.textChannel.canTalk()) {
            return
        }

        // Send a message if bot cant use embeds.
        if (!context.selfMember.hasPermission(context.textChannel, Permission.MESSAGE_EMBED_LINKS)) {
            context.textChannel.sendMessage(permissionToSpeak).queue()
            return
        }

        launch(CommonPool) {
            if (splitCommand(context)) {
                context.shard.requests++
            }
        }
    }

    private fun splitCommand(context: Context): Boolean {
        val content = context.message.rawContent.let {
            when {
                it.startsWith(Bot.CONFIG.prefix) -> it.substring(Bot.CONFIG.prefix.length)
                it.startsWith(namePrefix, true) -> it.substring(namePrefix.length)
                else -> {
                    val prefix = context.data.command.prefix
                    if (prefix == null || !it.startsWith(prefix)) return false
                    it.substring(prefix.length)
                }
            }
        }

        // Split the message. In house split for special syntax.
        val tokens = Utils.stringSplit(content)

        // Should not happen but as a guard.
        if (tokens.isEmpty()) {
            return false
        }

        val label = tokens[0].toLowerCase().trim()

        val cmd = Bot.getCommandRegistry().getCommand(label) ?: return false

        val args = tokens.copyOfRange(1, tokens.size)

        return callCommand(cmd, context, label, args)
    }


    /**
     * Call the command based on the message content.
     *
     * @return If the call was successful.
     */
    private fun callCommand(cmd: CommandExecutor, context: Context, label: String, args: Array<String>): Boolean {
        for (filter in filters) {
            if (!filter.test(cmd, context)) {
                return false
            }
        }

        try {
            cmd.execute(context, label, args)
            return true
        } catch (e: PermissionException) {
            context.send().error("The bot lacks the permission `${e.permission.getName()}` required to perform this command.").queue()
        } catch (e: Exception) {
            context.send().exception(e).queue()
            e.printStackTrace()
        }
        return false
    }

    fun sendHelp(context: Context, info: Command) {
        Bot.getCommandRegistry().getCommand("help").execute(context, "help", arrayOf(info.aliases.first()))
    }

//    // Checks for ratelimit
//    private fun isRateLimited(user: User) = System.currentTimeMillis() < cooldownMap.get(user.idLong)
//
//    // Set ratelimit
//    private fun rateLimit(user: User, ms: Long) {
//        if (ms != 0L) {
//            cooldownMap.put(user.idLong, System.currentTimeMillis() + ms)
//        }
//    }
}
