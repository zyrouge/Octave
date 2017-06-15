package xyz.gnarbot.gnar.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;

public class DiscordLogBack extends AppenderBase<ILoggingEvent> {
    private static boolean enabled;

    private PatternLayout patternLayout;

    public static void disable() {
        enabled = false;
    }

    public static void enable() {
        enabled = true;
    }

    private TextChannel consoleChannel() {
        if (!enabled) return null;
        if (Bot.CONFIG.getConsoleChannelID() == 0) return null;

        long id = Bot.CONFIG.getConsoleChannelID();

        for (Shard shard : Bot.getShards()) {
            TextChannel channel = shard.getTextChannelById(id);
            if (channel != null) {
                return channel;
            }
        }
        return null;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!enabled) return;

        TextChannel channel = consoleChannel();

        if (channel == null) return;

        if (!event.getLevel().isGreaterOrEqual(Level.INFO)) return;

        String content = patternLayout.doLayout(event);

        if (content.length() > 1920) {
            content = ":warning: Received a message but it was too long. " + Utils.hasteBin(content);
        }

        channel.sendMessage(content).queue();
    }

    @Override
    public void start() {
        patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern("`%d{HH:mm:ss}` `%t/%level` `%logger{0}` %msg");
        patternLayout.start();

        super.start();
    }
}