package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildData;

public class OptionsRegistry {
    public GuildData ofGuild(Guild guild) {
        GuildData data = Bot.db().getGuildData(guild.getId());
        return data == null ? new GuildData(guild.getId()) : data;
    }

    public void deleteGuild(long id) {
        Bot.db().deleteGuildData(Long.toUnsignedString(id));
    }

    public void deleteGuild(Guild guild) {
        deleteGuild(guild.getIdLong());
    }
}
