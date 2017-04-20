package xyz.gnarbot.gnar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class BotConfiguration {
    public final static File DATA_FOLDER = new File("data");
    public final static Config CONFIG = ConfigFactory.parseFile(new File(DATA_FOLDER, "bot.conf"))
            .withFallback(ConfigFactory.load("bot.conf"));

    public static final String BOT_NAME = CONFIG.getString("bot.name");
    public static final String BOT_GAME = CONFIG.getString("bot.game");

    public static final String PREFIX = CONFIG.getString("commands.prefix");
    public static final List<Long> ADMINISTRATORS = CONFIG.getLongList("commands.administrators");

    public static final int QUEUE_LIMIT = CONFIG.getInt("music.queue limit");
    public static final Duration DURATION_LIMIT = CONFIG.getDuration("music.duration limit");
    public static final String DURATION_LIMIT_TEXT = CONFIG.getString("music.duration limit");
    public static final Duration VOTE_SKIP_COOLDOWN = CONFIG.getDuration("music.vote skip cooldown");
    public static final String VOTE_SKIP_COOLDOWN_TEXT = CONFIG.getString("music.vote skip cooldown");

    public static final Color ACCENT_COLOR = Color.decode(CONFIG.getString("colors.accent"));
    public static final Color MUSIC_COLOR = Color.decode(CONFIG.getString("colors.alternate"));

    private BotConfiguration() {}
}
