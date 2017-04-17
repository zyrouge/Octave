package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.guilds.GuildData;

/**
 * Abstract class that is extended when creating a command.
 */
public abstract class CommandExecutor {
    Command commandInfo;

    GuildData guildData;

    public Shard getShard() {
        return guildData.getShard();
    }

    public Guild getGuild() {
        return guildData.getGuild();
    }

    public GuildData getGuildData() {
        return guildData;
    }

    public Bot getBot() {
        return getGuildData().getBot();
    }

    public Command getInfo() {
        return commandInfo;
    }

    /**
     * Abstract method to be executed when the command is called.
     * @param message Message object passed into the execution.
     * @param args Arguments passed into the execution.
     */
    protected abstract void execute(Message message, String[] args);
}