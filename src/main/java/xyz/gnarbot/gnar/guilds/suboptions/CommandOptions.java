package xyz.gnarbot.gnar.guilds.suboptions;

import java.util.HashSet;
import java.util.Set;

public class CommandOptions {
    private Set<String> allowedChannels;
    private Set<String> allowedUsers;
    private Set<String> allowedRoles;

    public Set<String> getAllowedChannels() {
        if (allowedChannels == null) {
            allowedChannels = new HashSet<>();
        }
        return allowedChannels;
    }

    public Set<String> getAllowedUsers() {
        if (allowedUsers == null) {
            allowedUsers = new HashSet<>();
        }
        return allowedUsers;
    }

    public Set<String> getAllowedRoles() {
        if (allowedRoles == null) {
            allowedRoles = new HashSet<>();
        }
        return allowedRoles;
    }
}
