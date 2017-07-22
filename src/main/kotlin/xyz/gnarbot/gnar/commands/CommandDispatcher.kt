package xyz.gnarbot.gnar.commands

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color

object CommandDispatcher {
    //private val cooldownMap = TLongLongHashMap()

    private val namePrefix = "${Bot.CONFIG.name.toLowerCase()} "

    fun handleEvent(event: GuildMessageReceivedEvent) {
        val guildOptions = Bot.getOptions().ofGuild(event.guild)

        val content = event.message.rawContent
        if (!content.startsWith(guildOptions.prefix)
                && !content.startsWith(Bot.CONFIG.prefix)
                && !content.startsWith(namePrefix, true)) {
            return
        }

        // Don't do anything if the bot can't even speak.
        if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_WRITE)) {
            return
        }

        // Send a message if bot cant use embeds.
        if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_EMBED_LINKS)) {
            event.channel.sendMessage("The bot needs the `" + Permission.MESSAGE_EMBED_LINKS.getName() +
                            "` permission in this channel to show messages.")
                    .queue(Utils.deleteMessage(15))
            return
        }

        launch(CommonPool) {
            val context = Context(event, guildOptions)

            if (splitCommand(context)) {
                context.shard.requests++
            }
        }
    }

    fun splitCommand(context: Context): Boolean {
        val content = context.message.rawContent?.let {
            when {
                it.startsWith(context.guildOptions.prefix) -> it.substring(context.guildOptions.prefix.length)
                it.startsWith(Bot.CONFIG.prefix) -> it.substring(Bot.CONFIG.prefix.length)
                it.startsWith(namePrefix, true) -> it.substring(namePrefix.length)
                else -> return false
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

        return callCommand(context, cmd, args)
    }

    /**
     * Call the command based on the message content.
     *
     * @return If the call was successful.
     */
    fun callCommand(context: Context, cmd: CommandExecutor, args: Array<String>) : Boolean {
        // Check if the person is to be ignored
        if (isIgnored(context, context.member)) {
            return false
        }

        val message = context.message
        val member = context.member

        // Bot administrator check.
        if (cmd.info.admin && member.user.idLong !in Bot.CONFIG.admins) {
            context.send().error("This command is for bot administrators only.").queue()
            return false
        }


        // Command settings check.
        if (!context.member.hasPermission(Permission.ADMINISTRATOR)) {
            context.guildOptions.commandOptions[cmd.info.id]?.let {
                if (it.allowedUsers.isNotEmpty() && context.user.id !in it.allowedUsers) {
                    context.send().error("You are not one of the users allowed to use this command.").queue()
                    return false
                }
                if (it.allowedRoles.isNotEmpty() && !it.allowedRoles.any { id -> context.member.roles.any { it.id == id } }) {
                    context.send().error("You don't have one of the roles allowed to use this command.").queue()
                    return false
                }
                if (it.allowedChannels.isNotEmpty() && context.channel.id !in it.allowedChannels) {
                    val channels = it.allowedChannels.map(context.guild::getTextChannelById)
                            .filterNotNull()
                            .map(IMentionable::getAsMention)
                            .joinToString(", ")

                    context.send().error("This command can only be used in $channels.").queue()
                    return false
                }
            }
        }

        // _<cmd> ? or _<cmd> help message
        // Delegate to _help <cmd>
        if (args.isNotEmpty() && (args[0] == "help" || args[0] == "?")) {
            Bot.getCommandRegistry().getCommand("help").execute(context, arrayOf(cmd.info.aliases.first()))
            return true
        }

        // Donator check
        if (cmd.info.donor && !context.guildOptions.isPremium) {
            context.send().embed("Donators Only") {
                color { Color.ORANGE }
                desc {
                    buildString {
                        append("🌟 This command is for donators' servers only.").ln()
                        append("In order to enjoy donator perks, please consider pledging to __**[our Patreon.](https://www.patreon.com/gnarbot)**__").ln()
                        append("Once you donate, join our __**[support guild](http://discord.gg/NQRpmr2)**__ and ask one of the owners.")
                    }
                }
            }.action().queue()
            return false
        }

        if (cmd.info.scope == Scope.VOICE) {
            if (member.voiceState.channel == null) {
                context.send().error("\uD83C\uDFB6 Music commands requires you to be in a voice channel.").queue()
                return false
            } else if (member.voiceState.channel == context.guild.afkChannel) {
                context.send().error("Music can't be played in the AFK channel.").queue()
                return false
            } else if (context.guildOptions.musicChannels.isNotEmpty()
                    && member.voiceState.channel.id !in context.guildOptions.musicChannels) {

                val channels = context.guildOptions.musicChannels
                        .map { context.guild.getVoiceChannelById(it) }
                        .filterNotNull()
                        .map(Channel::getName)

                context.send().error("Music can only be played in: `$channels`.").queue()
                return false
            }
        }

        if (cmd.info.permissions.isNotEmpty()) {
            val djRole = context.guildOptions.djRole?.let { context.guild.getRoleById(it) }
            val isDJ = djRole !in member.roles

            if (cmd.info.scope != Scope.VOICE || !isDJ) {
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
        } catch (e: Exception) {
            context.send().exception(e).queue()
            e.printStackTrace()
        }
        return false
    }

    // Ignore check:
    // Optional ignores: user, channel, role
    // Do not ignore if user have administrator role
    // Do not ignore if user is bot administrator
    private fun isIgnored(context: Context, member: Member): Boolean {
        return (context.guildOptions.ignoredUsers.contains(member.user.id)
                || context.guildOptions.ignoredChannels.contains(context.channel.id)
                || context.guildOptions.ignoredRoles.any { id -> member.roles.any { it.id == id } })
                && !member.hasPermission(Permission.ADMINISTRATOR)
                && member.user.idLong !in Bot.CONFIG.admins
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
