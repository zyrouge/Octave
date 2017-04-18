package xyz.gnarbot.gnar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.Color;
import java.io.File;
import java.util.List;

public class BotConfig {
    public final static File DATA_FOLDER = new File("data");
    public final static Config CONFIG = ConfigFactory.parseFile(new File(DATA_FOLDER, "bot.conf"));

    public static final String NAME = CONFIG.getString("name");
    public static final String PREFIX = CONFIG.getString("prefix");

    /** Bot administrator IDs. */
    public static final List<Long> ADMINISTRATORS = CONFIG.getLongList("administrators");

    /** Blocked users IDs. */
    public static final List<Long> BLOCKED_USERS = CONFIG.getLongList("blocked");

    public static final Color COLOR = new Color(0, 80, 175);
    public static final Color MUSIC_COLOR = new Color(0, 221, 88);
    public static final int QUEUE_LIMIT = 50;

    private BotConfig() {}
}
