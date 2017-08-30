package xyz.gnarbot.gnar.commands.executors.settings

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
import xyz.gnarbot.gnar.guilds.GuildData
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 54,
        aliases = arrayOf("commands", "cmd", "command", "cmds"),
        usage = "(enable|disable) [command] (...)",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.SETTINGS,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
// todo: simplify this shit
class ManageCommandsCommand : CommandTemplate() {
    @Executor(0, description = "Add a user to the command's allowed users list.")
    fun allow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            if (!it.allowedUsers.add(member.user.id)) {
                context.send().error("${member.asMention} is already in the allowed users list for `${info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Added ${member.asMention} to the allowed members list for `${info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(1, description = "Add a role to the command's allowed roles list.")
    fun allow_role_specific(context: Context, cmd: CommandExecutor, role: Role) {
        options(context, cmd) {
            if (!it.allowedRoles.add(role.id)) {
                context.send().error("${role.asMention} is already in the allowed roles list for `${info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Added ${role.asMention} to the allowed roles list for `${info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(2, description = "Add a channel to the command's allowed channels list.")
    fun allow_channel_specific(context: Context, cmd: CommandExecutor, channel: TextChannel) {
        options(context, cmd) {
            if (!it.allowedChannels.add(channel.id)) {
                context.send().error("${channel.asMention} is already in the allowed channels list for `${info.aliases.first()}`.").queue()
                return
            }

            context.data.save()

            context.send().embed("Command Management") {
                desc { "Added ${channel.asMention} to the allowed channels list for `${info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(3, description = "Add a user to the entire category's commands' user channels list.")
    fun allow_user_category(context: Context, category: Category, member: Member) {
        val groups = allowCategory(category, context.data, member.user.id) { it.allowedUsers }
        context.data.save()

        context.send().embed("Command Management") {
            groups["success"]?.let {
                field("Success") {
                    val successTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Added ${member.asMention} to the allowed users list for $successTxt."
                }
            }

            groups["alreadyAllowed"]?.let {
                field("Failed") {
                    val alreadyTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${member.asMention} is already in the allowed users list for $alreadyTxt."
                }
            }

            groups["untogglable"]?.let {
                field("Can't Toggle") {
                    val untogglableTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "$untogglableTxt can not be disabled."
                }
            }
        }.action().queue()
    }

    @Executor(4, description = "Add a role to the entire category's commands' allowed roles list.")
    fun allow_role_category(context: Context, category: Category, role: Role) {
        val groups = allowCategory(category, context.data, role.id) { it.allowedRoles }
        context.data.save()

        context.send().embed("Command Management") {
            groups["success"]?.let {
                field("Success") {
                    val successTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Added ${role.asMention} to the allowed roles list for $successTxt."
                }
            }

            groups["alreadyAllowed"]?.let {
                field("Failed") {
                    val alreadyTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${role.asMention} is already in the allowed roles list for $alreadyTxt."
                }
            }

            groups["untogglable"]?.let {
                field("Can't Toggle") {
                    val untogglableTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "$untogglableTxt can not be disabled."
                }
            }
        }.action().queue()
    }

    @Executor(5, description = "Add a channel to the entire category's commands' allowed channels list.")
    fun allow_channel_category(context: Context, category: Category, channel: TextChannel) {
        val groups = allowCategory(category, context.data, channel.id) { it.allowedChannels }
        context.data.save()

        context.send().embed("Command Management") {
            groups["success"]?.let {
                field("Success") {
                    val successTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Added ${channel.asMention} to the allowed channels list for $successTxt."
                }
            }

            groups["alreadyAllowed"]?.let {
                field("Failed") {
                    val alreadyTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${channel.asMention} is already in the allowed channels list for $alreadyTxt."
                }
            }

            groups["untogglable"]?.let {
                field("Can't Toggle") {
                    val untogglableTxt = it.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "$untogglableTxt can not be disabled."
                }
            }
        }.action().queue()
    }

    @Executor(6, description ="Remove a user from the command's allowed users list.")
    fun disallow_user_specific(context: Context, cmd: CommandExecutor, member: Member) {
        options(context, cmd) {
            if (it.allowedUsers.isEmpty()) {
                context.send().error("The allowed members list is empty, all members are able to use the command for `${info.aliases.first()}`.").queue()
                return
            }

            if (member.user.id !in it.allowedUsers) {
                context.send().error("${member.asMention} is not in the allowed users list for `${info.aliases.first()}`.").queue()
                return
            }

            it.allowedUsers.remove(member.user.id)
            context.data.save()

            context.send().embed("Command Management") {
                desc { "Removed ${member.asMention} from the allowed users list for `${info.aliases.first()}`." }
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
                context.send().error("${role.asMention} is not in the allowed roles list for `${info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${role.asMention} from the allowed channels list." }
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
                context.send().error("${channel.asMention} is not in the allowed channels list for `${info.aliases.first()}`.").queue()
                return
            }

            context.data.save()
            context.send().embed("Command Management") {
                desc { "Removed ${channel.asMention} from the allowed channels list for `${info.aliases.first()}`." }
            }.action().queue()
        }
    }

    @Executor(9, description = "Remove a user from the the entire category's commands' allowed users list.")
    fun disallow_user_category(context: Context, category: Category, member: Member) {
        val cmds = Bot.getCommandRegistry().entries

        val filtered = cmds.filter { it.info.category == category }
        if (filtered.isEmpty()) return

        val success = mutableListOf<Command>()
        val notAllowed = mutableListOf<Command>()

        filtered.map(CommandExecutor::getInfo).forEach { info ->
            context.data.command.options.getOrPut(info.id, ::CommandOptions).let {
                if (member.user.id !in it.allowedUsers) {
                    notAllowed.add(info)
                } else {
                    it.allowedUsers.remove(member.user.id)
                    success.add(info)
                }
            }
        }

        context.data.save()

        context.send().embed("Command Management") {
            if (success.isNotEmpty()) {
                field("Success") {
                    val successTxt = success.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Removed ${member.asMention} from the allowed users list for $successTxt."
                }
            }

            if (notAllowed.isNotEmpty()) {
                field("Failed") {
                    val notAllowedTxt = notAllowed.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${member.asMention} is not in the allowed users list for $notAllowedTxt."
                }
            }
        }.action().queue()
    }

    @Executor(10, description = "Remove a role from the the entire category's commands' allowed roles list.")
    fun disallow_role_category(context: Context, category: Category, role: Role) {
        val cmds = Bot.getCommandRegistry().entries

        val filtered = cmds.filter { it.info.category == category }
        if (filtered.isEmpty()) return

        val success = mutableListOf<Command>()
        val notAllowed = mutableListOf<Command>()

        filtered.map(CommandExecutor::getInfo).forEach { info ->
            context.data.command.options.getOrPut(info.id, ::CommandOptions).let {
                if (role.id !in it.allowedRoles) {
                    notAllowed.add(info)
                } else {
                    it.allowedRoles.remove(role.id)
                    success.add(info)
                }
            }
        }

        context.data.save()

        context.send().embed("Command Management") {
            if (success.isNotEmpty()) {
                field("Success") {
                    val successTxt = success.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Removed ${role.asMention} from the allowed roles list for $successTxt."
                }
            }

            if (notAllowed.isNotEmpty()) {
                field("Failed") {
                    val notAllowedTxt = notAllowed.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${role.asMention} is not in the allowed roles list for $notAllowedTxt."
                }
            }
        }.action().queue()
    }

    @Executor(11, description = "Remove a channel from the the entire category's commands' allowed channels list.")
    fun disallow_channel_category(context: Context, category: Category, channel: TextChannel) {
        val cmds = Bot.getCommandRegistry().entries

        val filtered = cmds.filter { it.info.category == category }
        if (filtered.isEmpty()) return

        val success = mutableListOf<Command>()
        val notAllowed = mutableListOf<Command>()

        filtered.map(CommandExecutor::getInfo).forEach { info ->
            context.data.command.options.getOrPut(info.id, ::CommandOptions).let {
                if (channel.id !in it.allowedChannels) {
                    notAllowed.add(info)
                } else {
                    it.allowedChannels.remove(channel.id)
                    success.add(info)
                }
            }
        }

        context.data.save()

        context.send().embed("Command Management") {
            if (success.isNotEmpty()) {
                field("Success") {
                    val successTxt = success.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "Removed ${channel.asMention} from the allowed channels list for $successTxt."
                }
            }

            if (notAllowed.isNotEmpty()) {
                field("Failed") {
                    val notAllowedTxt = notAllowed.joinToString("`, `", "`", "`") { it.aliases.first() }
                    "${channel.asMention} is not in the allowed channels list for $notAllowedTxt."
                }
            }
        }.action().queue()
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
                        }

                        it.mapNotNull(context.guild::getMemberById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }

            field("Allowed Roles") {
                buildString {
                    options.allowedRoles.let {
                        if (it.isEmpty()) {
                            append("The allowed roles list is empty. All roles are allowed to use this command.")

                        }

                        it.mapNotNull(context.guild::getRoleById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
                    }
                }
            }

            field("Allowed Channels") {
                buildString {
                    options.allowedChannels.let {
                        if (it.isEmpty()) {
                            append("The allowed channels list is empty. All channels are allowed to use this command.")

                        }

                        it.mapNotNull(context.guild::getTextChannelById)
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).append('\n') }
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
                "Cleared the command options for ${info.aliases.first()}."
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
        if (isNotValid(context, info)) return
        
        block(context.data.command.options.getOrPut(info.id, ::CommandOptions))
    }

    private fun isNotValid(context: Context, info: Command): Boolean {
        if (!info.toggleable) {
            context.send().error("`$info` can not be toggled.").queue()
            return true
        }

        return false
    }

    private fun allowCategory(category: Category, guildData: GuildData, id: String, transformSet: (CommandOptions) -> MutableSet<String>): Map<String, List<Command>> {
        val cmds = Bot.getCommandRegistry().entries

        val filtered = cmds.filter { it.info.category == category }
        if (filtered.isEmpty()) return emptyMap()

        return filtered.map(CommandExecutor::getInfo).groupBy { info ->
            if (!info.toggleable) {
                "untogglable"
            } else {
                guildData.command.options.getOrPut(info.id, ::CommandOptions).let {
                    val set = transformSet(it)
                    if (id in set) {
                        "alreadyAllowed"
                    } else {
                        set.add(id)
                        "success"
                    }
                }
            }
        }
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