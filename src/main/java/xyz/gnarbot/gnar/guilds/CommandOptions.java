package xyz.gnarbot.gnar.guilds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

public class CommandOptions {
    @JsonSerialize(contentAs = String.class)
    private Set<String> allowedChannels;

    @JsonSerialize(contentAs = String.class)
    private Set<String> allowedUsers;

    @JsonSerialize(contentAs = String.class)
    private Set<String> allowedRoles;

    @JsonIgnore
    public Set<String> getAllowedChannels() {
        if (allowedChannels == null) {
            allowedChannels = new HashSet<>();
        }
        return allowedChannels;
    }

    @JsonIgnore
    public Set<String> getAllowedUsers() {
        if (allowedUsers == null) {
            allowedUsers = new HashSet<>();
        }
        return allowedUsers;
    }

    @JsonIgnore
    public Set<String> getAllowedRoles() {
        if (allowedRoles == null) {
            allowedRoles = new HashSet<>();
        }
        return allowedRoles;
    }
}
