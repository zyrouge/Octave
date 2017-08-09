package xyz.gnarbot.gnar.db;

import net.dv8tion.jda.core.entities.Guild;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.guilds.GuildOptions;

public class OptionsRegistry {
    public GuildData ofGuild(Guild guild) {
        GuildData data = Bot.db().getGuildData(guild.getId());
        if (data == null) {
            // noinspection deprecation it is safe here
            GuildOptions options = Bot.db().getGuildOptions(guild.getId());

            if (options != null) {
                data = new GuildData(options);
                System.out.println("converted");
                data.save();
                //options.delete();
            } else {
                data = new GuildData(guild.getId());
                System.out.println("completly new");
            }
        } else {
            System.out.println("gottem");
        }
        return data;
    }

    public void deleteGuild(long id) {
        Bot.db().deleteGuildData(Long.toUnsignedString(id));
    }

    public void deleteGuild(Guild guild) {
        deleteGuild(guild.getIdLong());
    }
}
