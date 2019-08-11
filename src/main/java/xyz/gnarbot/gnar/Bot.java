package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.patreon.PatreonAPI;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.commands.dispatcher.CommandDispatcher;
import xyz.gnarbot.gnar.db.Database;
import xyz.gnarbot.gnar.db.OptionsRegistry;
import xyz.gnarbot.gnar.listeners.BotListener;
import xyz.gnarbot.gnar.listeners.PatreonListener;
import xyz.gnarbot.gnar.listeners.VoiceListener;
import xyz.gnarbot.gnar.music.PlayerRegistry;
import xyz.gnarbot.gnar.sentry.SentryUtil;
import xyz.gnarbot.gnar.utils.CountUpdater;
import xyz.gnarbot.gnar.utils.DatabaseManager;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;
import xyz.gnarbot.gnar.utils.SoundManager;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");

    private final Credentials credentials;
    private final Supplier<Configuration> configurationGenerator;
    private Configuration configuration;

    private final Database database;
    private final DatabaseManager db;
    private final Connection connection;

    private final OptionsRegistry optionsRegistry;
    private final PlayerRegistry playerRegistry;
    private final MyAnimeListAPI myAnimeListAPI;
    private final RiotApi riotApi;
    //private final DiscordFM discordFM;
    private final PatreonAPI patreon;
    private final CommandRegistry commandRegistry;
    private final CommandDispatcher commandDispatcher;
    private final EventWaiter eventWaiter;
    private final ShardManager shardManager;
    private final CountUpdater countUpdater;
    private final SoundManager soundManager;
    private final SentryUtil sentryUtil;

    public Bot(
            Credentials credentials,
            Supplier<Configuration> configurationGenerator
    ) throws LoginException {
        this.credentials = credentials;
        this.configurationGenerator = configurationGenerator;
        this.soundManager = new SoundManager();
        reloadConfiguration();
        ((ch.qos.logback.classic.Logger) LOG).setLevel(ch.qos.logback.classic.Level.DEBUG);

        LOG.info("Initializing the Discord bot.");

        this.database = new Database(getConnection(), this);
        this.db = new DatabaseManager(this, "");
        this.connection = db.establishConnection();

        if(connection == null) {
            LOG.error("Postgres connection failed. Make sure your information is correct.");
            System.exit(0);
        }
        optionsRegistry = new OptionsRegistry(this);

        String url = this.credentials.getWebHookURL();
        if (url != null) {
            LOG.info("Connected to Discord web hook.");
            //DiscordLogBack.enable(new WebhookClientBuilder(url).build());
        } else {
            LOG.warn("Not connected to Discord web hook.");
        }

        LOG.info("Name         :\t" + configuration.getName());
        LOG.info("Shards       :\t" + this.credentials.getTotalShards());
        LOG.info("Prefix       :\t" + configuration.getPrefix());
        LOG.info("Music Enabled:\t" + configuration.getMusicEnabled());
        LOG.info("Admins       :\t" + configuration.getAdmins());
        LOG.info("JDA Version  :\t" + JDAInfo.VERSION);

        eventWaiter = new EventWaiter();
        shardManager = new DefaultShardManagerBuilder()
                .setToken(credentials.getToken())
                .setMaxReconnectDelay(32)
                .setShardsTotal(credentials.getTotalShards())
                .setShards(credentials.getShardStart(), credentials.getShardEnd() - 1)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListeners(eventWaiter, new BotListener(this), new VoiceListener(this), new PatreonListener(this))
                .setActivityProvider(i -> Activity.playing(String.format(configuration.getGame(), i)))
                .setBulkDeleteSplittingEnabled(false)
                .build();

        countUpdater = new CountUpdater(this, shardManager);

        shardManager.addEventListener();

        LOG.info("The bot is now connecting to Discord.");

        playerRegistry = new PlayerRegistry(this, Executors.newSingleThreadScheduledExecutor());

        if (configuration.getMusicEnabled()) {
            soundManager.loadSounds();
            // SETUP APIs
            //discordFM = new DiscordFM(this);
            LOG.info("DiscordFM is temporarily disabled due to moving serves.");

        }

        patreon = new PatreonAPI(credentials.getPatreonToken());

        myAnimeListAPI = new MyAnimeListAPI(credentials.getMalUsername(), credentials.getMalPassword());
        String riotApiKey = credentials.getRiotAPIKey();
        ApiConfig apiConfig = new ApiConfig();
        if (riotApiKey != null) apiConfig.setKey(riotApiKey);
        riotApi = new RiotApi(new ApiConfig());

        commandRegistry = new CommandRegistry(this);
        commandDispatcher = new CommandDispatcher(this, commandRegistry, Executors.newWorkStealingPool());

        sentryUtil = new SentryUtil(this, Objects.requireNonNull(getCredentials().getSentryPubDsn()));

        LOG.info("Finish setting up bot internals.");
    }

    public void reloadConfiguration() {
        configuration = configurationGenerator.get();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public CountUpdater getCountUpdater() {
        return countUpdater;
    }

    public Guild getGuildById(long id) {
        return getJDA(MiscUtil.getShardForGuild(id, credentials.getTotalShards())).getGuildById(id);
    }

    public User getUserById(String id) {
        return getShardManager().getUserById(id);
    }

    public MyAnimeListAPI getMyAnimeListAPI() {
        return myAnimeListAPI;
    }

    public Database db() {
        return database;
    }

    public Connection getConnection() {
        return connection;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public PlayerRegistry getPlayers() {
        return playerRegistry;
    }

    public OptionsRegistry getOptions() {
        return optionsRegistry;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public JDA getJDA(int id) {
        return shardManager.getShardById(id);
    }

    public RiotApi getRiotAPI() {
        return riotApi;
    }

    public void restart() {
        LOG.info("Restarting the Discord bot shards.");
        shardManager.restart();
        LOG.info("Discord bot shards have now restarted.");
    }

    public boolean isLoaded() {
        return shardManager.getShardsRunning() == credentials.getTotalShards();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public PatreonAPI getPatreon() {
        return patreon;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseManager getDb() {
        return db;
    }
}
