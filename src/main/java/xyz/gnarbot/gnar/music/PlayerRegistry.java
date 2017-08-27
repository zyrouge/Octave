package xyz.gnarbot.gnar.music;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gnarbot.gnar.Bot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class PlayerRegistry {
    private final Logger LOG = LoggerFactory.getLogger("PlayerRegistry");

    private final TLongObjectMap<MusicManager> registry = new TLongObjectHashMap<>();

    public PlayerRegistry() {
        Bot.EXECUTOR.scheduleAtFixedRate(() -> clear(false), 20, 10, TimeUnit.MINUTES);
    }

    @Nonnull
    public MusicManager get(Guild guild) throws MusicLimitException {
        MusicManager manager = registry.get(guild.getIdLong());

        if (manager == null) {
            if (size() >= Bot.CONFIG.getMusicLimit() && !Bot.getOptions().ofGuild(guild).isPremium()) {
                throw new MusicLimitException();
            }

            manager = new MusicManager(guild);
            registry.put(guild.getIdLong(), manager);
        }

        return manager;
    }

    @Nullable
    public MusicManager getExisting(long id) {
        return registry.get(id);
    }

    @Nullable
    public MusicManager getExisting(Guild guild) {
        return getExisting(guild.getIdLong());
    }

    public void destroy(long id) {
        MusicManager manager = registry.get(id);
        if (manager != null) {
            manager.destroy();
            registry.remove(id);
        }
    }

    public void destroy(Guild guild) {
        destroy(guild.getIdLong());
    }

    public boolean contains(long id) {
        return registry.containsKey(id);
    }

    public boolean contains(Guild guild) {
        return registry.containsKey(guild.getIdLong());
    }

    public void shutdown() {
        clear(true);
    }

    public void clear(boolean force) {
        LOG.info("Cleaning up players (forceful: " + force + ")");
        TLongObjectIterator<MusicManager> iterator = registry.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            try {
                if (force
                        || !iterator.value().getGuild().getSelfMember().getVoiceState().inVoiceChannel()
                        || iterator.value().getPlayer().getPlayingTrack() == null) {
                    iterator.value().destroy();
                    iterator.remove();
                }
            } catch (Exception e) {
                LOG.warn("Exception occured while trying to clean up a guild", e);
            }
        }
        LOG.info("Finished cleaning up players.");
    }

    public TLongObjectMap<MusicManager> getRegistry() {
        return registry;
    }

    public int size() {
        return registry.size();
    }
}
