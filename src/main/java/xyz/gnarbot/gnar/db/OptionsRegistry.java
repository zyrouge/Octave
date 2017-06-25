package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildOptions;

public class OptionsRegistry {
    public GuildOptions ofGuild(long id) {
        return Bot.DATABASE.getGuildOptions(Long.toUnsignedString(id));
    }

    public GuildOptions ofGuild(Guild guild) {
        return ofGuild(guild.getIdLong());
    }

    public void deleteGuild(long id) {
        Bot.DATABASE.deleteGuildOptions(Long.toUnsignedString(id));
    }

    public void deleteGuild(Guild guild) {
        deleteGuild(guild.getIdLong());
    }
}
