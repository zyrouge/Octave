package xyz.gnarbot.gnar.listeners;

import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import xyz.gnarbot.gnar.guilds.GuildData;

public class AudioConnectionListener implements ConnectionListener {
    private final GuildData guildData;

    public AudioConnectionListener(GuildData guildData) {
        this.guildData = guildData;
    }

    @Override
    public void onPing(long l) {}

    @Override
    public void onStatusChange(ConnectionStatus status) {
        if (status == ConnectionStatus.CONNECTING_AWAITING_ENDPOINT
                && (guildData.getGuild().getRegion() == Region.SINGAPORE
                || guildData.getGuild().getRegion() == Region.UNKNOWN)) {
            guildData.getMusicManager().reset();
        }
    }

    @Override
    public void onUserSpeaking(User user, boolean b) {}
}
