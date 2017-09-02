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
    @Executor(-1, description = "Toggle admin bypass to roles and channels management.")
    fun toggle_admin_bypass(context: Context) {
        val value = context.data.command.isAdminBypass
        context.data.command.isAdminBypass = !value
        context.data.save()

        if (value) {
            context.send().embed("Command Management") {
                desc {
                    "Administrators will no longer bypass command management for roles and channels."
                }
            }.action().queue()
        } else {
            context.send().embed("Command Management") {
                desc {
                    "Administrators will now bypass command management for roles and channels."
                }
            }.action().queue()
        }
    }

    @Executor(0, description = "Add a user to the command's allowed users list.")
    fun allow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            allowTo(context, member.user.id, member.asMention, context.data.command.options,
                    ManageScope.USER, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(1, description = "Add a role to the command's allowed roles list.")
    fun allow_role_specific(context: Context, cmd: CommandExecutor, role: Role) {
        options(context, cmd) {
            allowTo(context, role.id, role.asMention, context.data.command.options,
                    ManageScope.ROLE, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(2, description = "Add a channel to the command's allowed channels list.")
    fun allow_channel_specific(context: Context, cmd: CommandExecutor, channel: TextChannel) {
        options(context, cmd) {
            allowTo(context, channel.id, channel.asMention, context.data.command.options,
                    ManageScope.CHANNEL, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(3, description = "Add a user to the entire category's commands' user channels list.")
    fun allow_user_category(context: Context, category: Category, member: Member) {
        options(context, category) {
            allowTo(context, member.user.id, member.asMention, context.data.command.categoryOptions,
                    ManageScope.USER, category.ordinal, category.title)
        }
    }

    @Executor(4, description = "Add a role to the entire category's commands' allowed roles list.")
    fun allow_role_category(context: Context, category: Category, role: Role) {
        options(context, category) {
            allowTo(context, role.id, role.asMention, context.data.command.categoryOptions,
                    ManageScope.ROLE, category.ordinal, category.title)
        }
    }

    @Executor(5, description = "Add a channel to the entire category's commands' allowed channels list.")
    fun allow_channel_category(context: Context, category: Category, channel: TextChannel) {
        options(context, category) {
            allowTo(context, channel.id, channel.asMention, context.data.command.categoryOptions,
                    ManageScope.CHANNEL, category.ordinal, category.title)
        }
    }

    @Executor(6, description ="Remove a user from the command's allowed users list.")
    fun disallow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            disallowTo(context, member.user.id, member.asMention, context.data.command.options,
                    ManageScope.USER, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(7, description = "Remove a role from the command's allowed roles list.")
    fun disallow_role_specific(context: Context, cmd: CommandExecutor, role: Role) {
        options(context, cmd) {
            disallowTo(context, role.id, role.asMention, context.data.command.options,
                    ManageScope.ROLE, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(8, description = "Remove a channel from the command's allowed channels list.")
    fun disallow_channel_specific(context: Context, cmd: CommandExecutor, channel: TextChannel) {
        options(context, cmd) {
            disallowTo(context, channel.id, channel.asMention, context.data.command.options,
                    ManageScope.CHANNEL, cmd.info.id, cmd.info.aliases[0])
        }
    }

    @Executor(9, description = "Remove a user from the the entire category's commands' allowed users list.")
    fun disallow_user_category(context: Context, category: Category, member: Member) {
        options(context, category) {
            disallowTo(context, member.user.id, member.asMention, context.data.command.categoryOptions,
                    ManageScope.USER, category.ordinal, category.title)
        }
    }

    @Executor(10, description = "Remove a role from the the entire category's commands' allowed roles list.")
    fun disallow_role_category(context: Context, category: Category, role: Role) {
        options(context, category) {
            disallowTo(context, role.id, role.asMention, context.data.command.categoryOptions,
                    ManageScope.ROLE, category.ordinal, category.title)
        }
    }

    @Executor(11, description = "Remove a channel from the the entire category's commands' allowed channels list.")
    fun disallow_channel_category(context: Context, category: Category, channel: TextChannel) {
        options(context, category) {
            disallowTo(context, channel.id, channel.asMention, context.data.command.categoryOptions,
                    ManageScope.CHANNEL, category.ordinal, category.title)
        }
    }

    @Executor(12, description = "Show the options of the command.")
    fun options_specific(context: Context, cmd: CommandExecutor) {
        val options = context.data.command.options[cmd.info.id]
        if (options == null) {
            context.send().embed("Command Management") {
                desc { "This command is allowed to everybody with the appropriate permission." }
            }.action().queue()
            return
        }

        sendOptionsFor(context, options, "command")
    }

    @Executor(13, description = "Show the options of the command.")
    fun options_category(context: Context, category: Category) {
        val options = context.data.command.categoryOptions[category.ordinal]
        if (options == null) {
            context.send().embed("Command Management") {
                desc { "This category is allowed to everybody with the appropriate permission." }
            }.action().queue()
            return
        }

        sendOptionsFor(context, options, "category")
    }

    private fun allowTo(context: Context, item: String, itemDisplay: String,
                        map: Map<Int, CommandOptions>, scope: ManageScope,
                        key: Int, keyDisplay: String) {
        if (!scope.transform(map, key).add(item)) {
            context.send().error(scope.alreadyAdded(itemDisplay, keyDisplay)).queue()
            return
        }

        context.data.save()
        context.send().embed("Command Management") {
            desc { scope.allowSuccess(itemDisplay, keyDisplay) }
        }.action().queue()
    }

    private fun disallowTo(context: Context, item: String, itemDisplay: String,
                           map: Map<Int, CommandOptions>, scope: ManageScope,
                           key: Int, keyDisplay: String) {
        val set = scope.transform(map, key)

        if (set.isEmpty()) {
            context.send().error(scope.empty()).queue()
            return
        }

        if (!set.remove(item)) {
            context.send().error(scope.notIn(itemDisplay, keyDisplay)).queue()
            return
        }

        context.data.save()
        context.send().embed("Command Management") {
            desc { scope.disallowSuccess(itemDisplay, keyDisplay) }
        }.action().queue()
    }

    private fun sendOptionsFor(context: Context, options: CommandOptions, title: String) {
        context.send().embed("Command Management") {
            field("Allowed Users") {
                buildString {
                    options.allowedUsers.let {
                        if (it.isEmpty()) {
                            append("The allowed users list is empty. All users are allowed to use this ")
                            append(title)
                            append('.')
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
                            append("The allowed roles list is empty. All roles are allowed to use this ")
                            append(title)
                            append('.')
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
                            append("The allowed channels list is empty. All channels are allowed to use this ")
                            append(title)
                            append('.')
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

    @Executor(14, description = "Clear all options for a command.")
    fun clear_command(context: Context, cmd: CommandExecutor) {
        val options = context.data.command.options[cmd.info.id]
        if (options == null) {
            context.send().embed("Command Management") {
                desc { "There's no options configured for this command." }
            }.action().queue()
            return
        }

        context.data.command.options.remove(cmd.info.id)
        context.data.save()

        context.send().embed("Command Management") {
            desc { "Cleared the command options for ${cmd.info.aliases.first()}." }
        }.action().queue()
    }

    @Executor(15, description = "Clear all options for a command.")
    fun clear_category(context: Context, category: Category) {
        val options = context.data.command.categoryOptions[category.ordinal]
        if (options == null) {
            context.send().embed("Command Management") {
                desc { "There's no options configured for this category." }
            }.action().queue()
            return
        }

        context.data.command.categoryOptions.remove(category.ordinal)
        context.data.save()

        context.send().embed("Command Management") {
            desc {
                "Cleared the category options for ${category.title}."
            }
        }.action().queue()
    }

    @Executor(16, description = "Clear all command options.")
    fun clear_all(context: Context) {
        if (context.data.command.options.isEmpty() && context.data.command.categoryOptions.isEmpty()) {
            context.send().error("This guild doesn't have any commands options.").queue()
            return
        }

        context.data.command.options.clear()
        context.data.command.categoryOptions.clear()
        context.data.save()

        context.send().embed("Command Management") {
            desc { "Cleared all command and category options." }
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

    private inline fun options(context: Context, category: Category, block: (CommandOptions) -> Unit) {
        block(context.data.command.categoryOptions.getOrPut(category.ordinal, ::CommandOptions))
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