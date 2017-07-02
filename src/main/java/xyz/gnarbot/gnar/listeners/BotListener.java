package xyz.gnarbot.gnar.listeners;


import gnu.trove.iterator.TLongObjectIterator;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.LoadState;
import xyz.gnarbot.gnar.commands.CommandDispatcher;
import xyz.gnarbot.gnar.guilds.GuildOptions;
import xyz.gnarbot.gnar.music.MusicManager;

public class BotListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            CommandDispatcher.INSTANCE.handleEvent(event);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            GuildOptions options = Bot.getOptions().ofGuild(event.getGuild());

            // Autorole
            if (options.getAutoRole() != null) {
                Role role = event.getGuild().getRoleById(options.getAutoRole());

                // If role is null then unset
                if (role == null) {
                    options.setAutoRole(null);
                    return;
                }

                // If bot cant interact with role then unset
                if (!event.getGuild().getSelfMember().canInteract(role)) {
                    options.setAutoRole(null);
                    return;
                }

                // Add the role to the member
                event.getGuild().getController().addRolesToMember(event.getMember(), role).reason("Auto-role").queue();
            }
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            // Destroy the player of the guild in which the bot left
            MusicManager manager = Bot.getPlayers().getExisting(event.getGuild());
            if (manager != null) {
                manager.getPlayer().destroy();
                Bot.getPlayers().getRegistry().remove(event.getGuild().getIdLong());
            }
            event.getGuild().getAudioManager().setSendingHandler(null);
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            if (event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent) {
                // If the bot left the channel, destroy player.
                if (event.getMember().getUser() == event.getJDA().getSelfUser()) {
                    Bot.getPlayers().destroy(event.getGuild().getIdLong());
                    return;
                }

                Guild guild = event.getGuild();
                if (guild == null) return;

                VoiceChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
                VoiceChannel channelLeft;

                // People left the bot's current channel.
                if (event instanceof GuildVoiceLeaveEvent) {
                    channelLeft = ((GuildVoiceLeaveEvent) event).getChannelLeft();
                } else {
                    channelLeft = ((GuildVoiceMoveEvent) event).getChannelLeft();
                }

                if (botChannel == null || !channelLeft.equals(botChannel)) return;

                // If the everyone left and the only remaining person is the bot
                // Size check will suffice since we already checked if its the bot earlier
                if (botChannel.getMembers().size() == 1) {
                    Bot.getPlayers().destroy(guild.getIdLong());
                }
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        Bot.LOG.info("JDA " + (event.getJDA().getShardInfo() != null ? event.getJDA().getShardInfo().getShardId() : "instance") + " is ready.");
    }

    @Override
    public void onResume(ResumedEvent event) {
        Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has resumed.");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has reconnected.");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        if (event.isClosedByServer()) {
            Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has disconnected (closed by server). "
                    + "Code: " + event.getServiceCloseFrame().getCloseCode() + " "  + event.getCloseCode());
        } else {
            Bot.LOG.info("JDA " + Bot.getShard(event.getJDA()).getId() + " has disconnected. "
                    + "Code: " + event.getClientCloseFrame().getCloseCode() + " " + event.getClientCloseFrame().getCloseReason());
        }

        // Clean up all of the MusicManagers associated with that shard.
        TLongObjectIterator<MusicManager> iterator = Bot.getPlayers().getRegistry().iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            if (iterator.value().getJda() == event.getJDA()) {
                iterator.value().getPlayer().destroy();
                iterator.remove();
            }
        }
    }

    @Override
    public void onException(ExceptionEvent event) {
        if (!event.isLogged()) Bot.LOG.error("Error thrown by JDA.", event.getCause());
    }
}
