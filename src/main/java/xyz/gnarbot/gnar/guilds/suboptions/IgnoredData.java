package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class IgnoredData {
    private Set<String> channels;
    private Set<String> users;
    private Set<String> roles;

    public IgnoredData() {}

    @JsonIgnore
    public IgnoredData(Set<String> channels, Set<String> users, Set<String> roles) {
        this.channels = channels;
        this.users = users;
        this.roles = roles;
    }

    @NotNull
    public final Set<String> getChannels() {
        if (channels == null) {
            channels = new HashSet<>();
        }
        return channels;
    }

    @NotNull
    public final Set<String> getUsers() {
        if (users == null) {
            users = new HashSet<>();
        }
        return users;
    }

    @NotNull
    public final Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }
}
