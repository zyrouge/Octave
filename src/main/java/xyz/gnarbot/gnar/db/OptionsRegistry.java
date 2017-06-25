package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildOptions;

public class OptionsRegistry {
    public GuildOptions ofGuild(long id) {
        return Bot.DATABASE.getGuildOptions(Long.toUnsignedString(id));
    }

    public GuildOptions ofGuild(Guild guild) {
        GuildOptions options = ofGuild(guild.getIdLong());
        return options != null ? options : new GuildOptions(guild.getId());
    }

    public void deleteGuild(long id) {
        Bot.DATABASE.deleteGuildOptions(Long.toUnsignedString(id));
    }

    public void deleteGuild(Guild guild) {
        deleteGuild(guild.getIdLong());
    }
}
