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
    public static final Logger LOG = LoggerFactory.getLogger("PlayerRegistry");

    private TLongObjectMap<MusicManager> registry = new TLongObjectHashMap<>();

    public PlayerRegistry() {
        Bot.EXECUTOR.scheduleAtFixedRate(() -> clear(false), 1, 1, TimeUnit.HOURS);
    }

    @Nonnull
    public MusicManager get(Guild guild) {
        if (size() >= 450) {
            throw new IllegalStateException("Music is currently at maximum capacity, please try again later.\n"
                    + "Please consider donating to our __**[Patreon](https://www.patreon.com/gnarbot)**__ to help us get better servers.");
        }

        MusicManager manager = registry.get(guild.getIdLong());

        if (manager == null) {
            manager = new MusicManager(guild.getIdLong(), guild.getJDA());
            registry.put(guild.getIdLong(), manager);
        }

        return manager;
    }

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
        for (MusicManager manager : registry.valueCollection()) {
            manager.destroy();
        }
        registry.clear();
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
                LOG.warn("Exception occured while trying to clean up guild " + iterator.value().getId(), e);
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
