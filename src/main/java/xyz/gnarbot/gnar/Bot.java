package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.listeners.GuildCountListener;
import xyz.gnarbot.gnar.utils.DiscordLogBack;
import xyz.gnarbot.gnar.utils.SimpleLogToSLF4JAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");

    public static final Credentials KEYS = new Credentials(new File("credentials.conf"));
    public static final BotConfiguration CONFIG = new BotConfiguration(new File("bot.conf"));

    private static final GuildCountListener guildCountListener = new GuildCountListener();
    private static final EventWaiter waiter = new EventWaiter();

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final List<Shard> shards = new ArrayList<>();

    public static void main(String[] args) {
        SimpleLogToSLF4JAdapter.install();
        DiscordLogBack.enable();

        LOG.info("Initializing the Discord bot.");

        LOG.info("Name:\t" + CONFIG.getName());
        LOG.info("Shards:\t" + CONFIG.getShards());
        LOG.info("Prefix:\t" + CONFIG.getPrefix());
        LOG.info("Admins:\t" + CONFIG.getAdmins().size());
        LOG.info("JDA:\t\t" + JDAInfo.VERSION);

        for (int i = 0; i < CONFIG.getShards(); i++) {
            shards.add(createShard(i));
        }

        LOG.info("The bot is now fully connected to Discord.");
    }

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static List<Shard> getShards() {
        return shards;
    }

    private static Shard createShard(int id) {
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(KEYS.getToken())
                .setAutoReconnect(true)
                .setAudioEnabled(true)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListener(guildCountListener, waiter)
                .setEnableShutdownHook(true)
                .setGame(Game.of(String.format(CONFIG.getGame(), id)));

        if (CONFIG.getShards() > 1) builder.useSharding(id, CONFIG.getShards());

        JDA jda = null;
        try {
            jda = builder.buildBlocking();

            jda.getSelfUser().getManager().setName(CONFIG.getName()).queue();
            LOG.info("JDA " + id + " is ready.");
            return new Shard(id, jda);
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void restart(int id) {
        LOG.info("Restarting the Discord bot shard $id.");

        shards.get(id).shutdown();

        shards.set(id, createShard(id));
    }

    public static void restart() {
        LOG.info("Restarting the Discord bot shards.");

        shards.stream().map(Shard::getId).forEach(Bot::restart);

        LOG.info("Discord bot shards have now restarted.");
    }

    /**
     * Stop the bot.
     */
    public static void stop() {
        shards.forEach(Shard::shutdown);

        LOG.info("Bot is now disconnected from Discord.");
    }
}
