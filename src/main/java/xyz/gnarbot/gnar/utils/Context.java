package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.utils.response.GuildResponseBuilder;
import xyz.gnarbot.gnar.utils.response.ResponseBuilder;

public final class Context {
    private final Message message;
    private final TextChannel textChannel;
    private final Guild guild;
    private final GuildData guildOptions;
    private final JDA jda;
    private final Shard shard;
    private final Member member;
    private final User user;

    public Context(GuildMessageReceivedEvent event) {
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = event.getChannel();
        this.guild = event.getGuild();
        this.guildOptions = Bot.getOptions().ofGuild(guild);
        this.member = event.getMember();
    }

    public Context(PrivateMessageReceivedEvent event) {
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = null;
        this.guild = null;
        this.guildOptions = null;
        this.member = null;
    }

    public final Message getMessage() {
        return this.message;
    }

    public final TextChannel getTextChannel() {
        return this.textChannel;
    }

    public final Guild getGuild() {
        return this.guild;
    }

    public final GuildData getData() {
        return this.guildOptions;
    }

    public final JDA getJDA() {
        return this.jda;
    }

    public final Shard getShard() {
        return this.shard;
    }

    public final Member getMember() {
        return this.member;
    }

    public final Member getSelfMember() {
        return this.guild.getSelfMember();
    }

    public final VoiceChannel getVoiceChannel() {
        return getMember().getVoiceState().getChannel();
    }

    public final User getUser() {
        return this.user;
    }

    public final ResponseBuilder send() {
        return new GuildResponseBuilder(textChannel);
    }
}
