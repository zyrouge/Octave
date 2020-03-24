package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.patreon.PatreonAPI;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.sentry.Sentry;
import net.dean.jraw.http.UserAgent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
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
import xyz.gnarbot.gnar.sharding.BucketedController;
import xyz.gnarbot.gnar.utils.CountUpdater;
import xyz.gnarbot.gnar.utils.DiscordFM;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;
import xyz.gnarbot.gnar.utils.SoundManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Bot {
    private static final Logger LOG = LoggerFactory.getLogger("Bot");
    private static Bot instance;
    private final BotCredentials credentials;
    private final Supplier<Configuration> configurationGenerator;
    private final UserAgent ua = new UserAgent("bot", "xyz.gnarbot.gnar", "5.1.2", "GnarDiscordBot");
    private final Bot bot = this; //strictly there for counter
    private final Database database;
    //private final RedditClient redditClient;
    private final OptionsRegistry optionsRegistry;
    private final PlayerRegistry playerRegistry;
    private final MyAnimeListAPI myAnimeListAPI;
    private final RiotApi riotApi;
    private final DiscordFM discordFM;
    private final PatreonAPI patreon;
    private final CommandRegistry commandRegistry;
    private final CommandDispatcher commandDispatcher;
    private final EventWaiter eventWaiter;
    private final ShardManager shardManager;
    private final SoundManager soundManager;
    private Configuration configuration;
    private CountUpdater countUpdater;
    private StatsDClient statsDClient = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    public static void main(String[] args) throws LoginException {
        new Bot(new BotCredentials(new File("credentials.conf")), () -> new Configuration(new File("bot.conf")));
    }

    public Bot(BotCredentials credentials, Supplier<Configuration> configurationGenerator) throws LoginException {
        this.credentials = credentials;
        this.configurationGenerator = configurationGenerator;
        this.soundManager = new SoundManager();
        soundManager.loadSounds();
        instance = this;
        reloadConfiguration();

        Sentry.init(configuration.getSentryDsn());

        LOG.info("Initializing the Discord bot.");

        database = new Database("bot");

        String url = this.credentials.getWebHookURL();
        if (url != null) {
            LOG.info("Connected to Discord web hook.");
        } else {
            LOG.warn("Not connected to Discord web hook.");
        }

        LOG.info("Name  :\t" + configuration.getName());
        LOG.info("Shards:\t" + this.credentials.getTotalShards());
        LOG.info("Prefix:\t" + configuration.getPrefix());
        LOG.info("Admins:\t" + configuration.getAdmins());
        LOG.info("JDA v.:\t" + JDAInfo.VERSION);
        reloadConfiguration();

        eventWaiter = new EventWaiter();
        shardManager = DefaultShardManagerBuilder.createDefault(credentials.getToken())
                .setSessionController(new BucketedController(configuration.getBucketFactor(), 215616923168276480L))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMaxReconnectDelay(32)
                .setShardsTotal(credentials.getTotalShards())
                .setShards(credentials.getShardStart(), credentials.getShardEnd() - 1)
                .setAudioSendFactory(new NativeAudioSendFactory(800))
                .addEventListeners(eventWaiter, new BotListener(this), new VoiceListener(this), new PatreonListener(this))
                .setActivityProvider(i -> Activity.playing(String.format(configuration.getGame(), i)))
                .setBulkDeleteSplittingEnabled(false)
                .build();

        LOG.info("The bot is now connecting to Discord. Bucket factor: " + configuration.getBucketFactor());

        optionsRegistry = new OptionsRegistry(this);
        playerRegistry = new PlayerRegistry(this, Executors.newSingleThreadScheduledExecutor());
        countUpdater = new CountUpdater(this);

        // SETUP APIs
        discordFM = new DiscordFM();

        patreon = new PatreonAPI(credentials.getPatreonToken());
        System.out.println("Patreon Established.");

        myAnimeListAPI = new MyAnimeListAPI(credentials.getMalUsername(), credentials.getMalPassword());
        String riotApiKey = credentials.getRiotAPIKey();
        ApiConfig apiConfig = new ApiConfig();
        if (riotApiKey != null)
            apiConfig.setKey(riotApiKey);

        riotApi = new RiotApi(new ApiConfig());

        commandRegistry = new CommandRegistry(this);
        commandDispatcher = new CommandDispatcher(this, commandRegistry, Executors.newWorkStealingPool());

        LOG.info("Finish setting up bot internals.");
    }

    public static Bot getInstance() {
        return instance;
    }

    public StatsDClient getDatadog() {
        return statsDClient;
    }

    public static Logger getLogger() {
        return LOG;
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

    public MyAnimeListAPI getMyAnimeListAPI() {
        return myAnimeListAPI;
    }

    public DiscordFM getDiscordFM() {
        return discordFM;
    }

    public Database db() {
        return database;
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

    public BotCredentials getCredentials() {
        return credentials;
    }

    public PatreonAPI getPatreon() {
        return patreon;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    //public RedditClient getRedditClient() {
    //    return redditClient;
    //}
}
