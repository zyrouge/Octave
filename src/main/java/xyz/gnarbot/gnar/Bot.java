package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.avarel.kaiper.interpreter.GlobalVisitorSettings;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.db.Database;
import xyz.gnarbot.gnar.db.OptionsRegistry;
import xyz.gnarbot.gnar.listener.VoiceListener;
import xyz.gnarbot.gnar.listeners.BotListener;
import xyz.gnarbot.gnar.music.PlayerRegistry;
import xyz.gnarbot.gnar.utils.DiscordFM;
import xyz.gnarbot.gnar.utils.DiscordLogBack;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;
import xyz.gnarbot.gnar.utils.SimpleLogToSLF4JAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");
    public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static final Credentials KEYS = new Credentials(new File("credentials.conf"));
    public static BotConfiguration CONFIG = new BotConfiguration(new File("bot.conf"));

    private static final MyAnimeListAPI malAPI = new MyAnimeListAPI(KEYS.getMalUsername(), KEYS.getMalPassword());

    private static final BotListener botListener = new BotListener();
    private static final VoiceListener voiceListener = new VoiceListener();
    private static final EventWaiter waiter = new EventWaiter();

    //private static final AsyncEventManager eventManager = new AsyncEventManager();

    private static final Database database = new Database("bot");
    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final OptionsRegistry optionsRegistry = new OptionsRegistry();
    private static final PlayerRegistry playerRegistry = new PlayerRegistry();

    private static final List<Shard> shards = new ArrayList<>(KEYS.getBotShards());

    public static LoadState STATE = LoadState.LOADING;

    public static void main(String[] args) throws InterruptedException {
        SimpleLogToSLF4JAdapter.install();

        String token = KEYS.getWebhookToken();
        if (token != null) {
            DiscordLogBack.enable(new WebhookClientBuilder(KEYS.getWebhookID(), token).build());
        }

        // KAIPER settings
        GlobalVisitorSettings.SIZE_LIMIT = 100;
        GlobalVisitorSettings.RECURSION_DEPTH_LIMIT = 10;
        GlobalVisitorSettings.ITERATION_LIMIT = 100;
        GlobalVisitorSettings.MILLISECONDS_LIMIT = 5;

        LOG.info("Initializing the Discord bot.");

        LOG.info("Name  :\t" + CONFIG.getName());
        LOG.info("Shards:\t" + KEYS.getBotShards());
        LOG.info("Prefix:\t" + CONFIG.getPrefix());
        LOG.info("Admins:\t" + CONFIG.getAdmins());
        LOG.info("JDA v.:\t" + JDAInfo.VERSION);

        DiscordFM.loadLibraries();

        for (int i = KEYS.getShardStart(); i < KEYS.getShardEnd(); i++) {
            Shard shard = new Shard(i, null, waiter, botListener, voiceListener);
            shards.add(shard);
            shard.buildAsync();
            Thread.sleep(5000);
        }

        STATE = LoadState.COMPLETE;

        for (Shard shard : shards) {
            shard.getJda().getPresence().setGame(Game.of(String.format(CONFIG.getGame(), shard.getId())));
        }

        LOG.info("The bot is now fully connected to Discord.");
    }

    public static void reloadConfig() {
        CONFIG = new BotConfiguration(new File("bot.conf"));
    }

    public static MyAnimeListAPI getMALAPI() {
        return malAPI;
    }

    public static Database db() {
        return database;
    }

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static PlayerRegistry getPlayers() {
        return playerRegistry;
    }

    public static OptionsRegistry getOptions() {
        return optionsRegistry;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static List<Shard> getShards() {
        return shards;
    }


    public static Guild getGuildById(long id) {
        return getShard((int) ((id >> 22) % KEYS.getBotShards())).getJda().getGuildById(id);
    }

    public static int getUserCount() {
        Set<Long> set = new HashSet<>();
        for (Shard shard : shards) {
            for (User user : shard.getJda().getUsers()) {
                set.add(user.getIdLong());
            }
        }
        return set.size();
    }

    public static Shard getShard(int id) {
        return shards.get(id);
    }

    public static Shard getShard(JDA jda) {
        return shards.get(jda.getShardInfo() != null ? jda.getShardInfo().getShardId() : 0);
    }


    public static void restart() {
        LOG.info("Restarting the Discord bot shards.");
        for (Shard shard : shards) {
            shard.revive();
        }
        LOG.info("Discord bot shards have now restarted.");
    }
}
