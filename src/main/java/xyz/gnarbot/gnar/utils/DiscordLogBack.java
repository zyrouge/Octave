package xyz.gnarbot.gnar.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DiscordLogBack extends AppenderBase<ILoggingEvent> {
    private static Bot bot;

    private static boolean enabled;

    private static final DateFormat format = new SimpleDateFormat("HH:mm:ss");
    private PatternLayout patternLayout;

    public static void disable() {
        DiscordLogBack.bot = null;
        enabled = false;
    }

    public static void enable(Bot bot) {
        DiscordLogBack.bot = bot;
        enabled = true;
    }

    private TextChannel consoleChannel() {
        if (!enabled) return null;
        if (bot.getConfig().getConsoleChannelID() == 0) return null;

        long id = bot.getConfig().getConsoleChannelID();

        for (Shard shard : bot.getShards()) {
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

        if (event.getMessage().length() > MessageEmbed.VALUE_MAX_LENGTH) {
            new ResponseBuilder(channel, bot).embed()
                    .setTitle(event.getLevel() + " | " + event.getLoggerName() + " | " + event.getThreadName())
                    .setDescription("Too long..." + Utils.hasteBin(patternLayout.doLayout(event)))
                    .setFooter(format.format(new Date(event.getTimeStamp())))
                    .action().queue();

            return;
        }

        new ResponseBuilder(channel, bot).embed()
                .setTitle(event.getLevel() + " - " + event.getLoggerName() + " - " + event.getThreadName())
                .setDescription(event.getMessage())
                .setFooter(format.format(new Date(event.getTimeStamp())))
                .action().queue();
    }

    @Override
    public void start() {
        patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern("[`%d{HH:mm:ss}`] [`%t/%level`] [`%logger{0}`]: %msg");
        patternLayout.start();

        super.start();
    }
}