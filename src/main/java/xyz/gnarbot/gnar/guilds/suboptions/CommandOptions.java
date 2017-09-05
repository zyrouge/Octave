package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"allowedChannels", "allowedUsers", "allowedRoles"})
public class CommandOptions {
    private Set<String> disabledChannels;
    private Set<String> disabledUsers;
    private Set<String> disabledRoles;

    public Set<String> getDisabledChannels() {
        if (disabledChannels == null) {
            disabledChannels = new HashSet<>();
        }
        return disabledChannels;
    }

    public Set<String> getDisabledUsers() {
        if (disabledUsers == null) {
            disabledUsers = new HashSet<>();
        }
        return disabledUsers;
    }

    public Set<String> getDisabledRoles() {
        if (disabledRoles == null) {
            disabledRoles = new HashSet<>();
        }
        return disabledRoles;
    }
}
