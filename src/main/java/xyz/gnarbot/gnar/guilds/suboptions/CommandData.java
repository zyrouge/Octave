package xyz.gnarbot.gnar.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import xyz.gnarbot.gnar.guilds.CommandOptions;

import java.util.HashMap;
import java.util.Map;

public class CommandData {
    private String prefix;
    private boolean autoDelete = false;

    @JsonSerialize(keyAs = Integer.class, contentAs = CommandOptions.class)
    private Map<Integer, CommandOptions> options;

    public CommandData() {}

    @JsonIgnore
    public CommandData(String prefix, boolean autoDelete, Map<Integer, CommandOptions> commandOptions) {
        this.prefix = prefix;
        this.autoDelete = autoDelete;
        this.options = commandOptions;
    }

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

    public Map<Integer, CommandOptions> getOptions() {
        if (options == null) {
            options = new HashMap<>();
        }
        return options;
    }
}
