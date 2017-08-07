package xyz.gnarbot.gnar.guilds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.db.ManagedObject;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties({"disabledCommands", "requestChannel"})
public final class GuildOptions implements ManagedObject {
    private final String id; // PORT


    String prefix; // PORT
    boolean autoDelete = false; // PORT

    @JsonSerialize(keyAs = Integer.class, contentAs = CommandOptions.class)
    Map<Integer, CommandOptions> commandOptions; // PORT

    Set<String> ignoredChannels; // PORTED
    Set<String> ignoredUsers; // PORTED
    Set<String> ignoredRoles; // PORTED

    String autoRole;
    Set<String> selfRoles;

    String djRole; // PORTED
    Set<String> musicChannels; // PORTED
    int musicVolume = 100; // PORTED
    boolean announce = true; // PORTED



    private long premiumUntil; // PORTED

    @ConstructorProperties("id")
    public GuildOptions(String id) {
        this.id = id;
    }


    @NotNull
    public final String getPrefix() {
        if (prefix == null) {
            prefix = Bot.CONFIG.getPrefix();
        }
        return prefix;
    }

    public final void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }



    @NotNull
    public final Set<String> getIgnoredChannels() {
        if (ignoredChannels == null) {
            ignoredChannels = new HashSet<>();
        }
        return ignoredChannels;
    }

    @NotNull
    public final Set<String> getIgnoredUsers() {
        if (ignoredUsers == null) {
            ignoredUsers = new HashSet<>();
        }
        return ignoredUsers;
    }

    @NotNull
    public final Set<String> getIgnoredRoles() {
        if (ignoredRoles == null) {
            ignoredRoles = new HashSet<>();
        }
        return ignoredRoles;
    }



    @Nullable
    public final String getAutoRole() {
        return autoRole;
    }

    public final void setAutoRole(String autoRole) {
        this.autoRole = autoRole;
    }

    @NotNull
    public final Set<String> getSelfRoles() {
        if (selfRoles == null) selfRoles = new HashSet<>();
        return selfRoles;
    }



    @Nullable
    public final String getDjRole() {
        return djRole;
    }

    public final void setDjRole(@Nullable String djRole) {
        this.djRole = djRole;
    }

    @NotNull
    public final Set<String> getMusicChannels() {
        if (musicChannels == null) musicChannels = new HashSet<>();
        return musicChannels;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public boolean isAnnounce() {
        return announce;
    }



    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }


    public Map<Integer, CommandOptions> getCommandOptions() {
        if (commandOptions == null) {
            commandOptions = new HashMap<>();
        }
        return commandOptions;
    }



    public final long getPremiumUntil() {
        return this.premiumUntil;
    }

    @JsonIgnore
    public final boolean isPremium() {
        return System.currentTimeMillis() < this.premiumUntil;
    }

    @JsonIgnore
    public final long remainingPremium() {
        return this.isPremium() ? this.premiumUntil - System.currentTimeMillis() : 0L;
    }

    @JsonIgnore
    public final void addPremium(long ms) {
        if (this.isPremium()) {
            this.premiumUntil += ms;
        } else {
            this.premiumUntil = System.currentTimeMillis() + ms;
        }
    }



    public void save() {
        Bot.DATABASE.saveGuildOptions(this);
    }

    public void delete() {
        Bot.DATABASE.deleteGuildOptions(this.id);
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    public String toString() {
        return "GuildOptions(id=" + this.id + ")";
    }
}
