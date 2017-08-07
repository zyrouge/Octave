package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class MusicData {
    private String djRole;
    private Set<String> musicChannels;
    private int musicVolume = 100;
    private boolean announce = true;

    public MusicData() {}

    @JsonIgnore
    public MusicData(String djRole, Set<String> musicChannels, int musicVolume, boolean announce) {
        this.djRole = djRole;
        this.musicChannels = musicChannels;
        this.musicVolume = musicVolume;
        this.announce = announce;
    }

    @Nullable
    public final String getDjRole() {
        return djRole;
    }

    public final void setDjRole(String id) {
        this.djRole = id;
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
}
