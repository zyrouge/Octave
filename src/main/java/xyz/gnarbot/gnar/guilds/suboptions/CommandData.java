package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CommandData {
    private String prefix;
    private boolean autoDelete = false;
    private boolean adminBypass = false;

    @JsonSerialize(keyAs = Integer.class, contentAs = CommandOptions.class)
    private Map<Integer, CommandOptions> options;

    @JsonSerialize(keyAs = Integer.class, contentAs = CommandOptions.class)
    private Map<Integer, CommandOptions> categoryOptions;

    @Nullable
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isAdminBypass() {
        return adminBypass;
    }

    public void setAdminBypass(boolean adminBypass) {
        this.adminBypass = adminBypass;
    }

    public Map<Integer, CommandOptions> getOptions() {
        if (options == null) {
            options = new HashMap<>();
        }
        return options;
    }

    public Map<Integer, CommandOptions> getCategoryOptions() {
        if (categoryOptions == null) {
            categoryOptions = new HashMap<>();
        }
        return categoryOptions;
    }
}
