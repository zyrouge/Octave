package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RoleData {
    private String autoRole;
    private Set<String> selfRoles;

    public RoleData() {}

    @JsonIgnore
    public RoleData(String autoRole, Set<String> selfRoles) {
        this.autoRole = autoRole;
        this.selfRoles = selfRoles;
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
}
