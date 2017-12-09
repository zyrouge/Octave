package xyz.avarel.core;

public class CommandInfo {
    private final String[] aliases;
    private final String description;
    private final String usage;

    public CommandInfo(Command annotation) {
        this(annotation.aliases(), annotation.description(), annotation.usage());
    }

    public CommandInfo(String[] aliases, String description, String usage) {
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
