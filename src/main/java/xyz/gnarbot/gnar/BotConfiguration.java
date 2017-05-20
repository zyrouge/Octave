package xyz.gnarbot.gnar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class BotConfiguration {
    // TODO use Configurate instead of typesafe/config

    public final static File DATA_FOLDER = new File("data");
    public final static Config CONFIG = ConfigFactory.parseFile(new File(DATA_FOLDER, "bot.conf"))
            .withFallback(ConfigFactory.load("bot.conf"));

    public static final String BOT_NAME = CONFIG.getString("bot.name");
    public static final String BOT_GAME = CONFIG.getString("bot.game");
    public static final int SHARDS = CONFIG.getInt("bot.shards");

    public static final String PREFIX = CONFIG.getString("commands.prefix");
    public static final List<Long> ADMINISTRATORS = CONFIG.getLongList("commands.administrators");

    public static final boolean MUSIC_ENABLED = CONFIG.getBoolean("music.enabled");
    public static final int QUEUE_LIMIT = CONFIG.getInt("music.queue limit");
    public static final Duration DURATION_LIMIT = CONFIG.getDuration("music.duration limit");
    public static final String DURATION_LIMIT_TEXT = CONFIG.getString("music.duration limit");
    public static final Duration VOTE_SKIP_COOLDOWN = CONFIG.getDuration("music.vote skip cooldown");
    public static final String VOTE_SKIP_COOLDOWN_TEXT = CONFIG.getString("music.vote skip cooldown");
    public static final Duration VOTE_SKIP_DURATION = CONFIG.getDuration("music.vote skip duration");
    public static final String VOTE_SKIP_DURATION_TEXT = CONFIG.getString("music.vote skip duration");
    public static final Duration SEARCH_DURATION = CONFIG.getDuration("music.search duration");
    public static final String SEARCH_DURATION_TEXT = CONFIG.getString("music.search duration");

    public static final Color ACCENT_COLOR = Color.decode(CONFIG.getString("colors.accent"));
    public static final Color MUSIC_COLOR = Color.decode(CONFIG.getString("colors.alternate"));

    private BotConfiguration() {}
}
