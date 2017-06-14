package xyz.gnarbot.gnar.listeners;


import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.utils.Context;

public class ShardListener extends ListenerAdapter {
    private final Shard shard;
    private final Bot bot;

    public ShardListener(Shard shard, Bot bot) {
        this.shard = shard;
        this.bot = bot;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith(bot.getConfig().getPrefix())) {
            GuildData gd = shard.getGuildData(event.getGuild());

            if (event.getAuthor() == null || event.getMember() == null) {
                return;
            }

            gd.handleCommand(new Context(event, gd, shard, bot));
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        shard.getGuildData().remove(event.getGuild().getIdLong());
    }

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
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
                GuildData data = shard.getGuildData(event.getGuild());
                data.getMusicManager().reset();
            }
        }
    }

    @Override
    public void onResume(ResumedEvent event) {
        bot.getLog().info("JDA " + shard.getId() + " has resumed.");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        bot.getLog().info("JDA " + shard.getId() + " has reconnected.");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        if (event.isClosedByServer()) {
            bot.getLog().info("JDA " + shard.getId() + " has disconnected (closed by server). "
                    + "Code: " + event.getServiceCloseFrame().getCloseCode() + " "  + event.getCloseCode());
        } else {
            bot.getLog().info("JDA " + shard.getId() + " has disconnected. "
                    + "Code: " + event.getClientCloseFrame().getCloseCode() + " " + event.getClientCloseFrame().getCloseReason());
        }
    }

    @Override
    public void onException(ExceptionEvent event) {
        if (!event.isLogged()) bot.getLog().error("Error thrown by JDA.", event.getCause());
    }
}
