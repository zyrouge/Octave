package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.guilds.GuildOptions;
import xyz.gnarbot.gnar.listeners.BotListener;
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
    private static final BotListener botListener = new BotListener();
    private static final EventWaiter waiter = new EventWaiter();

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final List<Shard> shards = new ArrayList<>();

    private static final TLongObjectMap<GuildData> guildDataMap = new TLongObjectHashMap<>();

    public static void main(String[] args) {
        SimpleLogToSLF4JAdapter.install();
        DiscordLogBack.enable();

        LOG.info("Initializing the Discord bot.");

        LOG.info("Name:\t" + CONFIG.getName());
        LOG.info("Shards:\t" + KEYS.getShards());
        LOG.info("Prefix:\t" + CONFIG.getPrefix());
        LOG.info("Admins:\t" + CONFIG.getAdmins().size());
        LOG.info("JDA:\t\t" + JDAInfo.VERSION);

        for (int i = 0; i < KEYS.getShards(); i++) {
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
                .addEventListener(guildCountListener, waiter, botListener)
                .setEnableShutdownHook(true)
                .setGame(Game.of(String.format(CONFIG.getGame(), id)));

        if (KEYS.getShards() > 1) builder.useSharding(id, KEYS.getShards());

        try {
            JDA jda = builder.buildBlocking();
            jda.getSelfUser().getManager().setName(CONFIG.getName()).queue();
            return new Shard(id, jda);
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TLongObjectMap<GuildData> getGuildDataMap() {
        return guildDataMap;
    }

    public static GuildData getGuildData(long id) {
        GuildData value = guildDataMap.get(id);
        if (value == null) {
            value = new GuildData(id, new GuildOptions());
            guildDataMap.put(id, value);
        }
        return value;
    }

    public static GuildData getGuildData(Guild guild) {
        return getGuildData(guild.getIdLong());
    }

    public static void clearGuildData(boolean interrupt) {
        TLongObjectIterator<GuildData> iterator = guildDataMap.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            if(iterator.value().reset(interrupt)) {
                iterator.remove();
            }
        }
    }

    public static Shard getShard(int id) {
        return shards.get(id);
    }

    public static Shard getShard(JDA jda) {
        return shards.get(jda.getShardInfo() != null ? jda.getShardInfo().getShardId() : 0);
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

    public static void stop() {
        shards.forEach(Shard::shutdown);
        LOG.info("Bot is now disconnected from Discord.");
    }
}
