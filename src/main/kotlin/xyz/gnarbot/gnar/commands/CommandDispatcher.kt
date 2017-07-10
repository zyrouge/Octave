package xyz.gnarbot.gnar.commands

import gnu.trove.map.hash.TLongLongHashMap
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Channel
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.Utils
import xyz.gnarbot.gnar.utils.ln
import java.awt.Color

object CommandDispatcher {
    private val cooldownMap = TLongLongHashMap()

    fun handleEvent(event: GuildMessageReceivedEvent) {
        val guildOptions = Bot.getOptions().ofGuild(event.guild)

        val content = event.message.rawContent
        if (!content.startsWith(guildOptions.prefix)
                && !content.startsWith(Bot.CONFIG.prefix)
                && !content.startsWith("gnar ", true)) {
            return
        }

        // Don't do anything if the bot can't even speak.
        if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_WRITE)) {
            return
        }

        // Send a message if bot cant use embeds.
        if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_EMBED_LINKS)) {
            event.channel.sendMessage("The bot needs the `${Permission.MESSAGE_EMBED_LINKS.getName()}}` permission to show messages.")
                    .queue(Utils.deleteMessage(15))
            return
        }

        launch(CommonPool) {
            val context = Context(event, guildOptions)

            if (callCommand(context)) {
                context.shard.requests++
            }
        }
    }

    /**
     * Call the command based on the message content.
     *
     * @param message Message object.
     * @return If the call was successful.
     */
    fun callCommand(context: Context) : Boolean {
        // Check if the person is to be ignored
        if (isIgnored(context, context.member)) {
            return false
        }

        // PROCESS TIMEOUT
        if (isRateLimited(context.user)) {
            val remainingTimeout = cooldownMap[context.user.idLong] - System.currentTimeMillis()
            context.send().text("\u23F1 **Too fast!** Try again in `${Utils.getTimestamp(remainingTimeout)}`.").queue()
            return false
        }

        // Prefix check
        val content = context.message.rawContent
        // Already checked.
        // if (!content.startsWith(Bot.CONFIG.prefix)) return false

        val contentPrefixStrip = if (content.startsWith(context.guildOptions.prefix)) {
            content.substring(context.guildOptions.prefix.length)
        } else if (content.startsWith(Bot.CONFIG.prefix)) {
            content.substring(Bot.CONFIG.prefix.length)
        } else if (content.startsWith("gnar ", true)) {
            content.substring("gnar ".length)
        } else {
            return false
        }

        // Split the message.
        val tokens = Utils.stringSplit(contentPrefixStrip)

        // shouldnt happen but oh well
        if (tokens.isEmpty()) {
            return false
        }

        val label = tokens[0].toLowerCase().trim()

        if (label in context.guildOptions.disabledCommands) {
            context.send().error("This command is disabled by the server owner.").queue()
            return false
        }


        val cmd = Bot.getCommandRegistry().getCommand(label) ?: return false
        val args = tokens.copyOfRange(1, tokens.size)

        rateLimit(context.user, cmd.info.cooldown)

        // _<cmd> ? or _<cmd> help message
        if (args.isNotEmpty() && (args[0] == "help" || args[0] == "?")) {
            context.send().embed("Command Information") {
                field("Aliases") { cmd.info.aliases.joinToString(", ") }
                field("Usage") { "${Bot.CONFIG.prefix}${cmd.info.aliases[0].toLowerCase()} ${cmd.info.usage}" }
                if (cmd.info.donor) {
                    field("Donator") {
                        "This command is exclusive to donators' guilds. Donate to our Patreon or PayPal to gain access to them."
                    }
                }

                if (cmd.info.permissions.isNotEmpty()) {
                    field("Required Permissions") { "${cmd.info.scope} ${cmd.info.permissions.map(Permission::getName)}" }
                }

                field("Description") { cmd.info.description }
            }.action().queue()
            return true
        }

        val message = context.message
        val member = context.member

        // Admins bypass permission requirements
        //
        val memberIsAdmin = member.user.idLong in Bot.CONFIG.admins
        if (!memberIsAdmin) {
            if (cmd.info.admin) {
                context.send().error("This command is for bot administrators only.").queue()
                return false
            }

            if (cmd.info.donor && !context.guildOptions.isPremium()) {
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
        }

        if (cmd.info.guildOwner && context.guild.owner != member) {
            context.send().error("This command is for server owners only.").queue()
            return false
        }

        if (cmd.info.category == Category.MUSIC) {
            val musicTextChannel = context.guildOptions.requestChannel?.let { context.guild.getTextChannelById(it) }
            if (musicTextChannel != null && musicTextChannel != context.channel) {
                context.send().error("\uD83C\uDFB6 Music commands in this guild can only be used in ${musicTextChannel.asMention}.").queue()
                return false
            }

            val djRole = context.guildOptions.djRole?.let { context.guild.getRoleById(it) }
            if (djRole != null && djRole !in member.roles) {
                context.send().error("\uD83C\uDFB6 Music commands in this guild can only be used by ${djRole.asMention}.").queue()
                return false
            }
        }

        if (cmd.info.scope == Scope.VOICE) {
            if (member.voiceState.channel == null) {
                context.send().error("\uD83C\uDFB6 Music commands requires you to be in a voice channel.").queue()
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

        if (!memberIsAdmin && cmd.info.permissions.isNotEmpty()) {
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

    // Checks for ratelimit
    private fun isRateLimited(user: User) = System.currentTimeMillis() < cooldownMap.get(user.idLong)

    // Set ratelimit
    private fun rateLimit(user: User, ms: Long) {
        if (ms != 0L) {
            cooldownMap.put(user.idLong, System.currentTimeMillis() + ms)
        }
    }
}
