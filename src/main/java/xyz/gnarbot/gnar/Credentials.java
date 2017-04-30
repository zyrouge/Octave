package xyz.gnarbot.gnar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Credentials {
    private final static Config CONFIG = ConfigFactory.parseFile(new File(BotConfiguration.DATA_FOLDER, "credentials.conf"))
            .withFallback(ConfigFactory.load("credentials.conf"));

    public final static String PRODUCTION = CONFIG.getString("token.production");
    public final static String BETA = CONFIG.getString("token.beta");

    public final static String ABAL = CONFIG.getString("server counts.abal");
    public final static String CARBONITEX = CONFIG.getString("server counts.carbonitex");
    public final static String DISCORDBOTS = CONFIG.getString("server counts.discordbots");

    public final static String IMGFLIP = CONFIG.getString("imgflip");
    public final static String MASHAPE = CONFIG.getString("mashape");

    private Credentials() {}
}
