package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildData;

public class Context extends CommandContext {
    private final GuildData guildOptions;

    public Context(GuildMessageReceivedEvent event) {
        super(event);
        this.guildOptions = Bot.getOptions().ofGuild(getGuild());
    }

    public Context(PrivateMessageReceivedEvent event) {
        super(event);
        this.guildOptions = null;
    }

    public GuildData getData() {
        return this.guildOptions;
    }
}
