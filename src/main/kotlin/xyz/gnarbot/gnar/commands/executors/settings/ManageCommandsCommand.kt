package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.IMentionable
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Description
import xyz.gnarbot.gnar.commands.template.Parser
import xyz.gnarbot.gnar.commands.template.Parsers
import xyz.gnarbot.gnar.guilds.suboptions.CommandOptions
import xyz.gnarbot.gnar.utils.Context

private val override: Map<Class<*>, Parser<*>> = HashMap(Parsers.PARSER_MAP).also {
    it.put(String::class.java,
            Parser<String>(
                    "@user|@role|@channel|*",
                    "Name or mention of a user, role, or channel",
                    { _, s -> s }
            )
    )
}

@Command(
        id = 54,
        aliases = arrayOf("commands", "cmd", "command", "cmds"),
        usage = "(enable|diable) (command|category) [command] (user|role|channel) (...)",
        description = "Manage usage of commands.",
        toggleable = false,
        category = Category.SETTINGS,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class ManageCommandsCommand : CommandTemplate(override) {
    @Description(value = "Enable a command for a user/role/channel.")
    fun enable_specific(context: Context, cmd: CommandExecutor, scope: ManageScope, entity: String) {
        options(context, cmd) {
            if (entity == "*") {
                allowAll(context, it, scope)
            } else entity(context, scope, entity)?.let { (item, itemDisplay) ->
                allowTo(context, item, itemDisplay, it, scope, cmd.info.aliases[0])
            }
        }
    }

    @Description(value = "Enable a category for a user/role/channel.")
    fun enable_category(context: Context, category: Category, scope: ManageScope, entity: String) {
        options(context, category) {
            if (entity == "*") {
                allowAll(context, it, scope)
            } else entity(context, scope, entity)?.let { (item, itemDisplay) ->
                allowTo(context, item, itemDisplay, it, scope, category.title)
            }
        }
    }

    @Description(value ="Disable a command for a user/role/channel.")
    fun disable_specific(context: Context, cmd: CommandExecutor, scope: ManageScope, entity: String) {
        options(context, cmd) {
            if (entity == "*") {
                disallowAll(context, it, scope)
            } else entity(context, scope, entity)?.let { (item, itemDisplay) ->
                disallowTo(context, item, itemDisplay, it, scope, cmd.info.aliases[0])
            }
        }
    }

    @Description(value = "Disable a category for a user/role/channel.")
    fun disable_category(context: Context, category: Category, scope: ManageScope, entity: String) {
        options(context, category) {
            if (entity == "*") {
                disallowAll(context, it, scope)
            } else entity(context, scope, entity)?.let { (item, itemDisplay) ->
                disallowTo(context, item, itemDisplay, it, scope, category.title)
            }
        }
    }

    @Description(value = "Show the options of the command.")
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

    @Description(value = "Show the options of the command.")
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

    private fun allowAll(context: Context, options: CommandOptions, scope: ManageScope) {
        val set = scope.transform(options)

        if (set.isEmpty()) {
            context.send().error("The command is not disabled for any ${scope.name.toLowerCase()}.").queue()
            return
        }

        set.clear()
        context.data.save()

        context.send().embed("Command Management") {
            desc { "Enabled the command for every ${scope.name.toLowerCase()}." }
        }.action().queue()
    }

    private fun allowTo(context: Context, id: String, target: String,
                        options: CommandOptions, scope: ManageScope, cmd: String) {
        val set = scope.transform(options)

        if (!set.remove(id)) {
            context.send().error("`$cmd` is not disabled for $target.").queue()
            return
        }

        context.data.save()
        context.send().embed("Command Management") {
            desc { "`$cmd` is now allowed for $target." }
        }.action().queue()
    }

    private fun disallowAll(context: Context, options: CommandOptions, scope: ManageScope) {
        val set = scope.transform(options)

        val all = scope.all(context)

        if (!set.addAll(all)) {
            context.send().error("The command is already disabled for every ${scope.name.toLowerCase()}.").queue()
            return
        }
        context.data.save()

        context.send().embed("Command Management") {
            desc { "Disabled the command for every ${scope.name.toLowerCase()}." }
        }.action().queue()
    }

    private fun disallowTo(context: Context, id: String, target: String,
                           options: CommandOptions, scope: ManageScope, cmd: String) {
        val set = scope.transform(options)

        if (!set.add(id)) {
            context.send().error("`$cmd` is already disabled for $target.").queue()
            return
        }

        context.data.save()
        context.send().embed("Command Management") {
            desc { "`$cmd` is now disabled for $target." }
        }.action().queue()
    }

    private fun sendOptionsFor(context: Context, options: CommandOptions, optionType: String) {
        context.send().embed("Command Management") {
            field("Disabled Users") {
                buildString {
                    options.disabledUsers.let {
                        if (it.isEmpty()) {
                            append("This ")
                            append(optionType)
                            append(" not disabled for any users.")
                        } else {
                            it.mapNotNull(context.guild::getMemberById)
                                    .map(IMentionable::getAsMention)
                                    .forEach { append("• ").append(it).append('\n') }
                        }
                    }
                }
            }

            field("Disabled Roles") {
                buildString {
                    options.disabledRoles.let {
                        if (it.isEmpty()) {
                            append("This ")
                            append(optionType)
                            append(" not disabled for any roles.")
                        } else {
                            it.mapNotNull(context.guild::getRoleById)
                                    .map(IMentionable::getAsMention)
                                    .forEach { append("• ").append(it).append('\n') }
                        }
                    }
                }
            }

            field("Disabled Channels") {
                buildString {
                    options.disabledChannels.let {
                        if (it.isEmpty()) {
                            append("This ")
                            append(optionType)
                            append(" not disabled for any channels.")
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

    @Description("Clear all options for a command.")
    fun clear_specific(context: Context, cmd: CommandExecutor) {
        clear(context, context.data.command.options, cmd.info.id, "category", cmd.info.aliases[0])
    }

    @Description("Clear all options for a command.")
    fun clear_category(context: Context, category: Category) {
        clear(context, context.data.command.categoryOptions, category.ordinal, "category", category.title)
    }

    private fun clear(context: Context, map: MutableMap<Int, CommandOptions>, key: Int, type: String, item: String) {
        val options = map[key]
        if (options == null) {
            context.send().embed("Command Management") {
                desc { "There's no options configured for this $type." }
            }.action().queue()
            return
        }

        map.remove(key)
        context.data.save()

        context.send().embed("Command Management") {
            desc {
                "Cleared the $type options for $item."
            }
        }.action().queue()
    }

    @Description("Clear all command options.")
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

    private fun entity(context: Context, scope: ManageScope, entity: String): Pair<String, String>? {
        return when(scope) {
            ManageScope.USER -> {
                val member = Parsers.MEMBER.parse(context, entity)

                if (member == null) {
                    context.send().error("Not a valid user name or mention.").queue()
                    return null
                }

                member.user.id to member.user.asMention
            }
            ManageScope.ROLE -> {
                val role = Parsers.ROLE.parse(context, entity)

                if (role == null) {
                    context.send().error("Not a valid role name or mention").queue()
                    return null
                }

                role.id to role.asMention
            }
            ManageScope.CHANNEL -> {
                val channel = Parsers.TEXT_CHANNEL.parse(context, entity)

                if (channel == null) {
                    context.send().error("Not a valid text channel name or mention.").queue()
                    return null
                }

                channel.id to channel.asMention
            }
        }
    }

    private inline fun options(context: Context, cmd: CommandExecutor, block: (CommandOptions) -> Unit) {
        cmd.info.let {
            if (!it.toggleable) {
                context.send().error("`${it.aliases[0]}` can not be toggled.").queue()
                return
            }

            block(context.data.command.options.getOrPut(it.id, ::CommandOptions))
        }
    }

    private inline fun options(context: Context, category: Category, block: (CommandOptions) -> Unit) {
        block(context.data.command.categoryOptions.getOrPut(category.ordinal, ::CommandOptions))
    }
}