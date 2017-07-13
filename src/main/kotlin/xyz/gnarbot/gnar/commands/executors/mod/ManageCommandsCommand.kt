package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Executor
import xyz.gnarbot.gnar.guilds.CommandOptions
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        id = 54,
        aliases = arrayOf("cmd", "command", "cmds", "commands"),
        usage = "(enable|disable) [command] (...)",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.MODERATION,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ManageCommandsCommand : CommandTemplate() {
    @Executor(0, description = "Allow a user to use this command.")
    fun allow(context: Context, cmd: String, member: Member) {
        val info = lookupCommand(cmd)?.info
        if (!isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (member.user.id in it.allowedUsers) {
                context.send().error("${member.asMention} is already in the allowed users list.").queue()
                return
            }

            it.allowedUsers.add(member.user.id)
            context.guildOptions.save()
        }
    }

    @Executor(1, description = "Allow a role to use this command.")
    fun allow(context: Context, cmd: String, role: Role) {
        val info = lookupCommand(cmd)?.info
        if (!isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (role.id in it.allowedRoles) {
                context.send().error("${role.asMention} is already in the allowed roles list.").queue()
                return
            }

            it.allowedRoles.add(role.id)
            context.guildOptions.save()
        }
    }

    @Executor(2, description = "Allow a channel to use this command.")
    fun allow(context: Context, cmd: String, channel: TextChannel) {
        val info = lookupCommand(cmd)?.info
        if (isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (channel.id in it.allowedChannels) {
                context.send().error("${channel.asMention} is already in the allowed channels list.").queue()
                return
            }

            it.allowedChannels.add(channel.id)
            context.guildOptions.save()
        }
    }

    @Executor(3, description = "Disallow an allowed user from using this command.")
    fun disallow(context: Context, cmd: String, member: Member) {
        val info = lookupCommand(cmd)?.info
        if (isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (it.allowedUsers.isEmpty()) {
                context.send().error("The allowed members list is empty, all members are able to use the command.").queue()
                return
            }

            if (member.user.id !in it.allowedUsers) {
                context.send().error("${member.asMention} is not in the allowed users list.").queue()
                return
            }

            it.allowedUsers.remove(member.user.id)
            context.guildOptions.save()
        }
    }

    @Executor(4, description = "Disallow an allowed role from using this command.")
    fun disallow(context: Context, cmd: String, role: Role) {
        val info = lookupCommand(cmd)?.info
        if (isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (it.allowedRoles.isEmpty()) {
                context.send().error("The allowed roles list is empty, all roles are able to use the command.").queue()
                return
            }

            if (role.id !in it.allowedRoles) {
                context.send().error("${role.asMention} is not in the allowed roles list.").queue()
                return
            }

            it.allowedRoles.remove(role.id)
            context.guildOptions.save()
        }
    }

    @Executor(5, description = "Disallow an allowed channel from using this command.")
    fun disallow(context: Context, cmd: String, channel: TextChannel) {
        val info = lookupCommand(cmd)?.info
        if (isNotValid(context, info)) return

        context.guildOptions.commandOptions.getOrPut(info?.id, ::CommandOptions).let {
            if (it.allowedChannels.isEmpty()) {
                context.send().error("The allowed channels list is empty, all channels are able to use the command.").queue()
                return
            }

            if (channel.id !in it.allowedChannels) {
                context.send().error("${channel.asMention} is in the allowed channels list.").queue()
                return
            }

            it.allowedChannels.remove(channel.id)
            context.guildOptions.save()

            context.send().embed("Command Management") {
                desc {
                    buildString {
                        append("Removed ${channel.asMention} from the allowed channels list.")
                    }
                }
            }.action().queue()
        }
    }

    @Executor(6, description = "Show the options of the command.")
    fun options(context: Context, cmd: String) {
        val info = lookupCommand(cmd)?.info

        if (info == null) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        }

        val options = context.guildOptions.commandOptions[info.id]
        if (options == null) {
            context.send().embed("Command Management") {
                desc {
                    "This command is allowed to everybody."
                }
            }.action().queue()
            return
        }

        context.send().embed("Command Management") {
            field("Allowed Users") {
                buildString {
                    options.allowedUsers.let {
                        if (it.isEmpty()) {
                            append("The allowed users list is empty. All users are allowed to use this command.")
                        }

                        it.map(context.guild::getMemberById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }

            field("Allowed Roles") {
                buildString {
                    options.allowedRoles.let {
                        if (it.isEmpty()) {
                            append("The allowed roles list is empty. All roles are allowed to use this command.")
                        }

                        it.map(context.guild::getRoleById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }

            field("Allowed Channels") {
                buildString {
                    options.allowedChannels.let {
                        if (it.isEmpty()) {
                            append("The allowed channels list is empty. All channels are allowed to use this command.")
                        }

                        it.map(context.guild::getTextChannelById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
        }.action().queue()
    }

    override fun noMatches(context: Context?, args: Array<out String>?) {
        noMatches(context, args, buildString {
            append("If you allow entities (user, role, channel) to use the command, ")
            append("everything else that's not the entities will not be able to use it.")
        })
    }

    private fun lookupCommand(label: String): CommandExecutor? {
        return Bot.getCommandRegistry().getCommand(label)
    }

    private fun isNotValid(context: Context, info: Command?): Boolean {
        if (info == null) {
            context.send().error("`$info` is not a valid command.").queue()
            return true
        } else if (!info.toggleable) {
            context.send().error("`$info` can not be toggled.").queue()
            return true
        }

        val aliases = info.aliases

        if (aliases.isEmpty()) {
            context.send().error("`$info` is not a valid command.").queue()
            return true
        }

        return false
    }
}