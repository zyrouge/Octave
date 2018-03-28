package xyz.gnarbot.gnar.db.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties("djRole")
public class MusicData {
    private Set<String> musicChannels;
    private int volume = 100;
    private boolean announce = true;

    @NotNull
    public final Set<String> getChannels() {
        if (musicChannels == null) musicChannels = new HashSet<>();
        return musicChannels;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int musicVolume) {
        this.volume = musicVolume;
    }

    public boolean getAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }
}
