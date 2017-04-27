package xyz.gnarbot.gnar.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils

class CommandDispatcher(private val bot: Bot) {
    /** @returns Enabled command entries. */
    val enabled: List<CommandExecutor> get() = bot.commandRegistry.entries.apply { removeAll(disabled) }.toList()

    /** @returns Disabled command entries. */
    val disabled: MutableList<CommandExecutor> = mutableListOf()

    /**
     * Call the command based on the message content.
     *
     * @param message Message object.
     * @return If the call was successful.
     */
    fun callCommand(context: Context) : Boolean {
        val content = context.message.content
        if (!content.startsWith(BotConfiguration.PREFIX)) return false

        // Tokenize the message.
        val tokens = Utils.stringSplit(content, ' ')

        val label = tokens[0].substring(BotConfiguration.PREFIX.length).toLowerCase()

        val args = tokens.copyOfRange(1, tokens.size)
        
        val cmd = bot.commandRegistry.getCommand(label) ?: return false

        if (cmd in disabled) {
            context.send().error("This command is disabled by the server owner.").queue()
            return false
        }

        val message = context.message
        val member = message.member

        if (cmd.info.administrator) {
            if (member.user.idLong !in BotConfiguration.ADMINISTRATORS) {
                context.send().error("This command is for bot administrators only.").queue()
                return false
            }
        }
        if (cmd.info.guildOwner) {
            if (context.guild.owner != member) {
                context.send().error("This command is for server owners only.").queue()
                return false
            }
        }

        if (cmd.info.permissions.isNotEmpty()) {
            if (cmd.info.scope == Scope.VOICE) {
                if (member.voiceState.channel == null) {
                    context.send().error("This command requires you to be in a voice channel.").queue()
                    return false
                }
            }
            if (!cmd.info.scope.checkPermission(context, *cmd.info.permissions)) {
                val requirements = cmd.info.permissions.map(Permission::getName)
                context.send().error("You lack the following permissions: `$requirements` in " + when (cmd.info.scope) {
                    Scope.GUILD -> "the guild `${message.guild.name}`."
                    Scope.TEXT -> "the text channel `${message.textChannel.name}`."
                    Scope.VOICE -> "the voice channel `${member.voiceState.channel.name}`."
                }).queue()
                return false
            }
        }

        try {
            cmd.execute(context, args)
            return true
        } catch (e: PermissionException) {
            context.send().error("The bot lacks the permission `${e.permission.getName()}` required to perform this command.").queue()
        } catch (e: RuntimeException) {
            context.send().exception(e).queue()
            e.printStackTrace()
        }
        return false
    }

    /**
     * Enable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun enableCommand(cmd: CommandExecutor) : CommandExecutor? {
        if (cmd !in disabled) return null
        disabled -= cmd
        return cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun enableCommand(label: String) : CommandExecutor? {
        return bot.commandRegistry.getCommand(label)?.let(this::enableCommand)
    }

    /**
     * Disable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun disableCommand(cmd: CommandExecutor) : CommandExecutor? {
        if (cmd in disabled) return null
        if (!cmd.info.disableable) return null
        disabled += cmd
        return cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun disableCommand(label: String) : CommandExecutor? {
        return bot.commandRegistry.getCommand(label)?.let(this::disableCommand)
    }
}
