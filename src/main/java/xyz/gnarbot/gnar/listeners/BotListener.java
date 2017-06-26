package xyz.gnarbot.gnar.listeners;


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
            if (event.getMessage().getContent().startsWith(Bot.CONFIG.getPrefix())) {
                CommandDispatcher.INSTANCE.handleEvent(event);
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            GuildOptions options = Bot.getOptions().ofGuild(event.getGuild());

            if (options.getAutoRole() != null) {
                Role role = event.getGuild().getRoleById(options.getAutoRole());

                if (role == null) {
                    options.setAutoRole(null);
                    return;
                }

                if (!event.getGuild().getSelfMember().canInteract(role)) {
                    options.setAutoRole(null);
                    return;
                }

                event.getGuild().getController().addRolesToMember(event.getMember(), role).reason("Auto-role").queue();
            }
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
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
                if (event.getMember().getUser() == event.getJDA().getSelfUser()) return;

                Guild guild = event.getGuild();

                if (guild == null) return;

                VoiceChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

                VoiceChannel channelLeft;

                if (event instanceof GuildVoiceLeaveEvent) {
                    channelLeft = ((GuildVoiceLeaveEvent) event).getChannelLeft();
                } else {
                    channelLeft = ((GuildVoiceMoveEvent) event).getChannelLeft();
                }

                if (botChannel == null || !channelLeft.equals(botChannel)) return;

                if (botChannel.getMembers().size() == 1) {
                    Bot.getPlayers().destroy(guild);
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
    }

    @Override
    public void onException(ExceptionEvent event) {
        if (!event.isLogged()) Bot.LOG.error("Error thrown by JDA.", event.getCause());
    }
}
