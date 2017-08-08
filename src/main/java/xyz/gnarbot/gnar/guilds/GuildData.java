package xyz.gnarbot.gnar.guilds;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private CommandData commandData;
    private RoleData roleData;
    private IgnoredData ignoredData;
    private MusicData musicData;

    @JsonSerialize(keyAs = String.class, contentAs = Long.class)
    private Map<String, Long> premiumKeys;

    @ConstructorProperties("id")
    public GuildData(String id) {
        this.id = id;
    }

    @JsonIgnore
    public GuildData(GuildOptions opt) {
        this(opt.getId());

        if (opt.ignoredChannels != null && !opt.ignoredChannels.isEmpty()
                || opt.ignoredUsers != null && !opt.ignoredUsers.isEmpty()
                || opt.ignoredRoles != null && !opt.ignoredRoles.isEmpty()) {
            ignoredData = new IgnoredData(opt.ignoredChannels, opt.ignoredUsers, opt.ignoredRoles);
        }

        if (opt.djRole != null
                || opt.musicChannels != null && !opt.musicChannels.isEmpty()
                || opt.musicVolume != 100
                || !opt.announce) {
            musicData = new MusicData(opt.djRole, opt.musicChannels, opt.musicVolume, opt.announce);
        }

        if (opt.prefix != null
                || !opt.autoDelete
                || opt.commandOptions != null && !opt.commandOptions.isEmpty()) {
            commandData = new CommandData(opt.prefix, opt.autoDelete, opt.commandOptions);
        }

        if (opt.autoRole != null
                || opt.selfRoles != null && !opt.selfRoles.isEmpty()) {
            roleData = new RoleData(opt.autoRole, opt.selfRoles);
        }

        if (opt.isPremium()) {
            premiumKeys = new HashMap<>();
            premiumKeys.put("init", opt.getPremiumUntil());
        }
    }

    public void addPremiumKey(String id, long duration) {
        if (!isPremium()) {
            premiumKeys.clear();
            premiumKeys.put("init", System.currentTimeMillis());
        }

        premiumKeys.put(id, duration);
    }

    public long getPremiumUntil() {
        if (premiumKeys == null) {
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

    public boolean isPremium() {
        return System.currentTimeMillis() < getPremiumUntil();
    }

    public String getId() {
        return id;
    }

    public CommandData getCommand() {
        if (commandData == null) commandData = new CommandData();
        return commandData;
    }

    public IgnoredData getIgnored() {
        if (ignoredData == null) ignoredData = new IgnoredData();
        return ignoredData;
    }

    public MusicData getMusic() {
        if (musicData == null) musicData = new MusicData();
        return musicData;
    }

    public RoleData getRoles() {
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
