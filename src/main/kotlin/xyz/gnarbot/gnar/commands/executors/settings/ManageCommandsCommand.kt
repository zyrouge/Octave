package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Executor
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 54,
        aliases = arrayOf("commands", "cmd", "command", "cmds"),
        usage = "(allow|disallow) (user|role|channel) (specific|category) [command] (...)",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.SETTINGS,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ManageCommandsCommand : CommandTemplate() {
    enum class ManageScope { // TODO
        USER {
            override fun alreadyAdded(name: String, target: String) : String {
                return "$name is already in the allowed users list for `$target`."
            }

            override fun success(name: String, target: String): String {
                return "Added $name to the allowed members list for `$target`."
            }
        };

        abstract fun alreadyAdded(name: String, target: String) : String

        abstract fun success(name: String, target: String) : String
    }

    @Executor(0, description = "Add a user to the command's allowed users list.")
    fun allow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            if (!it.allowedUsers.add(member.user.id)) {
                context.send().error(ManageScope.USER.alreadyAdded(member.asMention, cmd.info.aliases.first())).queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { ManageScope.USER.success(member.asMention, cmd.info.aliases.first()) }
            }.action().queue()
        }
    }

    @Executor(1, description = "Add a role to the command's allowed roles list.")
    fun allow_role_specific(context: Context, cmd: CommandExecutor, role: Role) {
        options(context, cmd) {
            if (!it.allowedRoles.add(role.id)) {
                context.send().error("${role.asMention} is already in the allowed roles list for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Added ${role.asMention} to the allowed roles list for `${cmd.info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(2, description = "Add a channel to the command's allowed channels list.")
    fun allow_channel_specific(context: Context, cmd: CommandExecutor, channel: TextChannel) {
        options(context, cmd) {
            if (!it.allowedChannels.add(channel.id)) {
                context.send().error("${channel.asMention} is already in the allowed channels list for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            context.data.save()

            context.send().embed("Command Management") {
                desc { "Added ${channel.asMention} to the allowed channels list for `${cmd.info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(3, description = "Add a user to the entire category's commands' user channels list.")
    fun allow_user_category(context: Context, category: Category, member: Member) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (!it.allowedUsers.add(member.user.id)) {
                context.send().error("${member.asMention} is already in the allowed users list for `${category.title}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Added ${member.asMention} to the allowed members list for `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(4, description = "Add a role to the entire category's commands' allowed roles list.")
    fun allow_role_category(context: Context, category: Category, role: Role) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (!it.allowedRoles.add(role.id)) {
                context.send().error("${role.asMention} is already in the allowed roles list for `${category.title}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Added ${role.asMention} to the allowed roles list for `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(5, description = "Add a channel to the entire category's commands' allowed channels list.")
    fun allow_channel_category(context: Context, category: Category, channel: TextChannel) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (!it.allowedChannels.add(channel.id)) {
                context.send().error("${channel.asMention} is already in the allowed channels list for `${category.title}`.").queue()
                return
            }

            context.data.save()

            context.send().embed("Command Management") {
                desc { "Added ${channel.asMention} to the allowed channels list for `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(6, description ="Remove a user from the command's allowed users list.")
    fun disallow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            if (it.allowedUsers.isEmpty()) {
                context.send().error("The allowed members list is empty, all members are able to use the command for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            if (member.user.id !in it.allowedUsers) {
                context.send().error("${member.asMention} is not in the allowed users list for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            it.allowedUsers.remove(member.user.id)
            context.data.save()

            context.send().embed("Command Management") {
                desc { "Removed ${member.asMention} from the allowed users list for `${cmd.info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(7, description = "Remove a role from the command's allowed roles list.")
    fun disallow_role_specific(context: Context, cmd: CommandExecutor, role: Role) {
        options(context, cmd) {
            if (it.allowedRoles.isEmpty()) {
                context.send().error("The allowed roles list is empty, all roles are able to use the command.").queue()
                return
            }

            if (!it.allowedRoles.remove(role.id)) {
                context.send().error("${role.asMention} is not in the allowed roles list for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${role.asMention} from the allowed channels list for `${cmd.info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(8, description = "Remove a channel from the command's allowed channels list.")
    fun disallow_channel_specific(context: Context, cmd: CommandExecutor, channel: TextChannel) {
        options(context, cmd) {
            if (it.allowedChannels.isEmpty()) {
                context.send().error("The allowed channels list is empty, all channels are able to use the command.").queue()
                return
            }

            if (!it.allowedChannels.remove(channel.id)) {
                context.send().error("${channel.asMention} is not in the allowed channels list for `${cmd.info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${channel.asMention} from the allowed channels list for `${cmd.info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(9, description = "Remove a user from the the entire category's commands' allowed users list.")
    fun disallow_user_category(context: Context, category: Category, member: Member) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (it.allowedUsers.isEmpty()) {
                context.send().error("The allowed members list is empty, all members are able to use the category's commands.").queue()
                return
            }

            if (member.user.id !in it.allowedUsers) {
                context.send().error("${member.asMention} is not in the allowed users list for `${category.title}`.").queue()
                return
            }

            it.allowedUsers.remove(member.user.id)
            context.data.save()

            context.send().embed("Command Management") {
                desc { "Removed ${member.asMention} from the allowed users list for category `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(10, description = "Remove a role from the the entire category's commands' allowed roles list.")
    fun disallow_role_category(context: Context, category: Category, role: Role) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (it.allowedRoles.isEmpty()) {
                context.send().error("The allowed roles list is empty, all roles are able to use the category's commands.").queue()
                return
            }

            if (!it.allowedRoles.remove(role.id)) {
                context.send().error("${role.asMention} is not in the allowed roles list for `${category.title}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${role.asMention} from the allowed channels list for category `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(11, description = "Remove a channel from the the entire category's commands' allowed channels list.")
    fun disallow_channel_category(context: Context, category: Category, channel: TextChannel) {
        context.data.command.categoryOptions.getOrPut(info.id, ::CommandOptions).let {
            if (it.allowedChannels.isEmpty()) {
                context.send().error("The allowed channels list is empty, all channels are able to use the category's commands.").queue()
                return
            }

            if (!it.allowedChannels.remove(channel.id)) {
                context.send().error("${channel.asMention} is not in the allowed channels list for `${category.title}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${channel.asMention} from the allowed channels list for `${category.title}`." }
            }.action().queue()
        }
    }

    @Executor(12, description = "Show the options of the command.")
    fun options(context: Context, cmd: CommandExecutor) {
        val info = cmd.info

        if (info == null) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        }

        val options = context.data.command.options[info.id]
        if (options == null) {
            context.send().embed("Command Management") {
                desc {
                    "This command is allowed to everybody with the appropriate permission."
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
                        } else {
                            it.mapNotNull(context.guild::getMemberById)
                                    .map(IMentionable::getAsMention)
                                    .forEach { append("• ").append(it).append('\n') }
                        }
                    }
                }
            }

            field("Allowed Roles") {
                buildString {
                    options.allowedRoles.let {
                        if (it.isEmpty()) {
                            append("The allowed roles list is empty. All roles are allowed to use this command.")
                        } else {
                            it.mapNotNull(context.guild::getRoleById)
                                    .map(IMentionable::getAsMention)
                                    .forEach { append("• ").append(it).append('\n') }
                        }
                    }
                }
            }

            field("Allowed Channels") {
                buildString {
                    options.allowedChannels.let {
                        if (it.isEmpty()) {
                            append("The allowed channels list is empty. All channels are allowed to use this command.")
                        } else {
                            it.mapNotNull(context.guild::getTextChannelById)
                                    .map(IMentionable::getAsMention)
                                    .forEach { append("• ").append(it).append('\n') }
                        }
                    }
                }
            }
        }.action().queue()
    }

    @Executor(13, description = "Clear all options for a command.")
    fun clear_command(context: Context, cmd: CommandExecutor) {
        val info = cmd.info

        if (info == null) {
            context.send().error("`$cmd` is not a valid command.").queue()
            return
        }

        val options = context.data.command.options[info.id]
        if (options == null) {
            context.send().embed("Command Management") {
                desc {
                    "There's no options configured for this command."
                }
            }.action().queue()
            return
        }

        context.data.command.options.remove(info.id)
        context.data.save()

        context.send().embed("Command Management") {
            desc {
                "Cleared the command options for ${cmd.info.aliases.first()}."
            }
        }.action().queue()
    }

    @Executor(14, description = "Clear all command options.")
    fun clear_all(context: Context) {
        if (context.data.command.options.isEmpty()) {
            context.send().error("This guild doesn't have any commands options.").queue()
            return
        }

        context.data.command.options.clear()
        context.data.save()

        context.send().embed("Command Management") {
            desc {
                "Cleared the command options."
            }
        }.action().queue()
    }
    
    private inline fun options(context: Context, cmd: CommandExecutor, block: (CommandOptions) -> Unit) {
        val info = cmd.info

        if (!info.toggleable) {
            context.send().error("`$info` can not be toggled.").queue()
            return
        }
        
        block(context.data.command.options.getOrPut(info.id, ::CommandOptions))
    }

    override fun helpMessage(context: Context, args: Array<out String>) {
        helpMessage(context, args, null, buildString {
            append("If you allow entities (user, role, channel) to use the command, ")
            append("everything else will not not be able to use the command unless you include ")
            append("those also. If you do not explicitly allow anyone to use the command, it ")
            append("will be usable to everyone.")
        })
    }
}