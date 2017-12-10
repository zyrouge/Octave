package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.guilds.GuildData;
import xyz.gnarbot.gnar.utils.response.GuildResponseBuilder;
import xyz.gnarbot.gnar.utils.response.ResponseBuilder;

public class Context {
    private final GuildData guildOptions;
    private Message message;
    private TextChannel textChannel;
    private Guild guild;
    private JDA jda;
    private Shard shard;
    private Member member;
    private User user;

    public Context(GuildMessageReceivedEvent event) {
        this.jda = event.getJDA();
        this.shard = Bot.getShard(this.jda);

        this.message = event.getMessage();
        this.user = event.getAuthor();

        this.textChannel = event.getChannel();
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.guildOptions = Bot.getOptions().ofGuild(getGuild());
    }

    public GuildData getData() {
        return this.guildOptions;
    }

    public Message getMessage() {
        return this.message;
    }

    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public Shard getShard() {
        return this.shard;
    }

    public Member getMember() {
        return this.member;
    }

    public Member getSelfMember() {
        return this.guild.getSelfMember();
    }

    public VoiceChannel getVoiceChannel() {
        return getMember().getVoiceState().getChannel();
    }

    public User getUser() {
        return this.user;
    }

    public ResponseBuilder send() {
        return new GuildResponseBuilder(getTextChannel());
    }
}
