package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OptionsRegistry {
    @Nonnull
    public GuildData ofGuild(Guild guild) {
        GuildData data = Bot.db().getGuildData(guild.getId());
        return data == null ? new GuildData(guild.getId()) : data;
    }

    @Nullable
    public GuildData ofGuild(long id) {
        Guild guild = Bot.getGuildById(id);
        return guild == null ? null : ofGuild(guild);
    }

    public void deleteGuild(long id) {
        Bot.db().deleteGuildData(Long.toUnsignedString(id));
    }

    public void deleteGuild(Guild guild) {
        deleteGuild(guild.getIdLong());
    }
}
