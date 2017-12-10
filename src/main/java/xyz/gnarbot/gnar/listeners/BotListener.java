package xyz.gnarbot.gnar.listeners;


import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.LoadState;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.music.MusicManager;

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BotListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            if (event.getGuild().getSelfMember().getJoinDate().isBefore(OffsetDateTime.now().minusSeconds(30))) {
                return;
            }

            Bot.LOG.info("✅ Joined `" + event.getGuild().getName() + "`");
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            if (event.getAuthor().isBot()) {
                if (event.getAuthor() == event.getJDA().getSelfUser()) {
                    if (Bot.getOptions().ofGuild(event.getGuild()).getCommand().isAutoDelete()) {
                        event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
                    }
                }
                return;
            }

            Bot.getCommandDispatcher().handle(new Context(event));
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            GuildData options = Bot.getOptions().ofGuild(event.getGuild());

            // Autorole
            if (options.getRoles().getAutoRole() != null) {
                Role role = event.getGuild().getRoleById(options.getRoles().getAutoRole());

                // If role is null then unset
                if (role == null) {
                    options.getRoles().setAutoRole(null);
                    return;
                }

                // If bot cant interact with role then unset
                if (!event.getGuild().getSelfMember().canInteract(role)
                        || !event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                    options.getRoles().setAutoRole(null);
                    return;
                }

                // Add the role to the member
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).reason("Auto-role").queue();
            }
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if (Bot.STATE == LoadState.COMPLETE) {
            Bot.getPlayers().destroy(event.getGuild());
            Bot.LOG.info("❌ Left `" + event.getGuild().getName() + "`");
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
        Iterator<Map.Entry<Long, MusicManager>> iterator = Bot.getPlayers().getRegistry().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, MusicManager> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
                Bot.LOG.warn("Null manager for id " + entry.getKey());
            } else if (entry.getValue().getGuild().getJDA() == event.getJDA()) {
                entry.getValue().destroy();
                iterator.remove();
            }
        }
    }

    @Override
    public void onException(ExceptionEvent event) {
        if (!event.isLogged()) Bot.LOG.error("Error thrown by JDA.", event.getCause());
    }
}
