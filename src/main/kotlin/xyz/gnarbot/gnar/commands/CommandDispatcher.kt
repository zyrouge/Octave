package xyz.gnarbot.gnar.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color

class CommandDispatcher {
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
        if (!content.startsWith(Bot.CONFIG.prefix)) return false

        // Split the message.
        val tokens = Utils.stringSplit(content)

        val label = tokens[0].substring(Bot.CONFIG.prefix.length).toLowerCase().trim()

        val args = tokens.copyOfRange(1, tokens.size)

        val cmd = Bot.getCommandRegistry().getCommand(label) ?: return false

        if (cmd in disabled) {
            context.send().error("This command is disabled by the server owner.").queue()
            return false
        }

        val message = context.message
        val member = message.member

        if (member.user.idLong !in Bot.CONFIG.admins) {
            if (cmd.info.admin) {
                context.send().error("This command is for bot administrators only.").queue()
                return false
            }

            if (cmd.info.donor && !context.guildData.isPremium()) {
                context.send().embed("Donators Only") {
                    setColor(Color.ORANGE)
                    description {
                        buildString {
                            append("🌟 This command is for donators' servers only.").ln()
                            append("In order to enjoy donator perks, please consider pledging to __**[our Patreon.](https://www.patreon.com/gnarbot)**__").ln()
                            append("Once you donate, join our __**[support guild](http://discord.gg/NQRpmr2)**__ and ask one of the owners.")
                        }
                    }
                }.action().queue()
                return false
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
        return Bot.getCommandRegistry().getCommand(label)?.let(this::enableCommand)
    }

    /**
     * Disable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun disableCommand(cmd: CommandExecutor) : CommandExecutor? {
        if (cmd in disabled || !cmd.info.toggleable) return null
        disabled += cmd
        return cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun disableCommand(label: String) : CommandExecutor? {
        return Bot.getCommandRegistry().getCommand(label)?.let(this::disableCommand)
    }
}
