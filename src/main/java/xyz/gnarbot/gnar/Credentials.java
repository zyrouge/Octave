package xyz.gnarbot.gnar;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Credentials {
    private final static Config credentials_config = ConfigFactory.parseFile(new File(BotConfig.DATA_FOLDER, "credentials.conf"));

    public final static String PRODUCTION = credentials_config.getString("token.production");
    public final static String BETA = credentials_config.getString("token.beta");

    public final static String MARVEL_PU = credentials_config.getString("marvel.pu");
    public final static String MARVEL_PR = credentials_config.getString("marvel.pr");

    public final static String ABAL_URL = credentials_config.getString("abal.url");
    public final static String ABAL_TOKEN = credentials_config.getString("abal.token");

    public final static String LEAUGE = credentials_config.getString("leauge");
    public final static String IMGFLIP = credentials_config.getString("imgflip");

    public final static String CARBONITEX = credentials_config.getString("carbonitex");

    public final static String MASHAPE = credentials_config.getString("mashape");

    private Credentials() {}
}
