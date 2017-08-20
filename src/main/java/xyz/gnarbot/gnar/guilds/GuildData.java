package xyz.gnarbot.gnar.guilds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.ManagedObject;
import xyz.gnarbot.gnar.guilds.suboptions.CommandData;
import xyz.gnarbot.gnar.guilds.suboptions.IgnoredData;
import xyz.gnarbot.gnar.guilds.suboptions.MusicData;
import xyz.gnarbot.gnar.guilds.suboptions.RoleData;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class GuildData implements ManagedObject {
    private final String id;

    @JsonSerialize
    @JsonDeserialize(as = CommandData.class)
    private CommandData commandData;

    @JsonSerialize
    @JsonDeserialize(as = RoleData.class)
    private RoleData roleData;

    @JsonSerialize
    @JsonDeserialize(as = IgnoredData.class)
    private IgnoredData ignoredData;

    @JsonSerialize
    @JsonDeserialize(as = MusicData.class)
    private MusicData musicData;

    @JsonSerialize(keyAs = String.class, contentAs = Long.class)
    @JsonDeserialize(keyAs = String.class, contentAs = Long.class)
    private Map<String, Long> premiumKeys;

    @ConstructorProperties("id")
    public GuildData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addPremiumKey(String id, long duration) {
        if (premiumKeys == null) premiumKeys = new HashMap<>();

        if (!isPremium()) {
            premiumKeys.clear();
            premiumKeys.put("init", System.currentTimeMillis());
        }

        premiumKeys.put(id, duration);
    }

    @JsonIgnore
    public Map<String, Long> getPremiumKeys() {
        if (premiumKeys == null) premiumKeys = new HashMap<>();
        return premiumKeys;
    }

    @JsonIgnore
    public long getPremiumUntil() {
        if (premiumKeys == null || premiumKeys.isEmpty()) {
            return 0;
        }

        long premiumUntil = 0;
        for (long duration : premiumKeys.values()) {
            premiumUntil += duration;
        }
        return premiumUntil;
    }

    @JsonIgnore
    public final long remainingPremium() {
        return this.isPremium() ? getPremiumUntil() - System.currentTimeMillis() : 0L;
    }

    @JsonIgnore
    public boolean isPremium() {
        return System.currentTimeMillis() < getPremiumUntil();
    }

    @JsonIgnore
    public CommandData getCommand() {
        if (commandData == null) commandData = new CommandData();
        return commandData;
    }

    @JsonIgnore
    public IgnoredData getIgnored() {
        if (ignoredData == null) ignoredData = new IgnoredData();
        return ignoredData;
    }

    @JsonIgnore
    public MusicData getMusic() {
        if (musicData == null) musicData = new MusicData();
        return musicData;
    }

    @JsonIgnore
    public RoleData getRoles() {
        if (roleData == null) roleData = new RoleData();
        return roleData;
    }

    @Override
    public void save() {
        Bot.db().saveGuildData(this);
    }

    @Override
    public void delete() {
        Bot.db().deleteGuildData(id);
    }
}
