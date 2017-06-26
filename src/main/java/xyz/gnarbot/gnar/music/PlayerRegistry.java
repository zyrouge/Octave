package xyz.gnarbot.gnar.music;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerRegistry {
    public static final Logger LOG = LoggerFactory.getLogger("PlayerRegistry");

    private TLongObjectMap<MusicManager> registry = new TLongObjectHashMap<>();

    @Nonnull
    public MusicManager get(Guild guild) {
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

    public void clear() {
        for (MusicManager manager : registry.valueCollection()) {
            manager.destroy();
        }
        registry.clear();
    }

    public void clear(boolean force) {
        TLongObjectIterator<MusicManager> iterator = registry.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            if (force || iterator.value().getPlayer().getPlayingTrack() == null) {
                iterator.remove();
            }
        }
    }

    public TLongObjectMap<MusicManager> getRegistry() {
        return registry;
    }

    public int size() {
        return registry.size();
    }
}
